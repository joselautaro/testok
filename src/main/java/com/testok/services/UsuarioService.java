package com.testok.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.testok.entities.Rol;
import com.testok.entities.Usuario;
import com.testok.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ======================================================
       LISTADOS SEGÚN ROL
       ====================================================== */

    /**
     * 🔐 ADMIN: solo ve clientes (USER)
     */
    public List<Usuario> listarSoloClientes() {
        return usuarioRepository.findAllByRol(Rol.USER);
    }

    /**
     * 🔐 SUPERADMIN: ve ADMIN + USER
     * (NO mostramos SUPERADMIN por seguridad)
     */
    public List<Usuario> listarAdminsYClientesParaSuperadmin() {
        return usuarioRepository.findAllByRolIn(List.of(Rol.USER, Rol.ADMIN));
    }

    /* ======================================================
       CRUD
       ====================================================== */

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado con id=" + id));
    }

    public Usuario crearUsuario(String username, String rawPassword, Rol rol) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        username = username.trim();

        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username.");
        }

        if (rawPassword == null || rawPassword.trim().length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }

        if (rol == null) {
            rol = Rol.USER;
        }

        // 🔐 Solo SUPERADMIN puede crear algo que no sea USER
        if (!esSuperAdmin() && rol != Rol.USER) {
            throw new IllegalArgumentException(
                    "Solo SUPERADMIN puede crear usuarios con rol ADMIN.");
        }

        // Nunca crear SUPERADMIN desde UI
        if (rol == Rol.SUPERADMIN) {
            throw new IllegalArgumentException(
                    "No se permite crear SUPERADMIN desde el formulario.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    /**
     * ✏️ EDITAR USUARIO
     */
    public Usuario editarUsuario(Long id,
                                 String usernameNuevo,
                                 String passwordNuevaOpcional,
                                 Rol rolNuevo) {

        Usuario target = buscarPorId(id);

        // Nadie edita SUPERADMIN
        if (target.getRol() == Rol.SUPERADMIN) {
            throw new AccessDeniedException("No se puede editar un SUPERADMIN.");
        }

        // ADMIN solo edita USER
        if (esAdmin() && !esSuperAdmin()) {
            if (target.getRol() != Rol.USER) {
                throw new AccessDeniedException(
                        "Un ADMIN solo puede editar usuarios CLIENTE.");
            }
            if (rolNuevo != null && rolNuevo != Rol.USER) {
                throw new AccessDeniedException(
                        "Un ADMIN no puede asignar rol ADMIN.");
            }
        }

        // SUPERADMIN puede cambiar rol entre USER / ADMIN
        if (rolNuevo != null) {
            if (rolNuevo == Rol.SUPERADMIN) {
                throw new AccessDeniedException(
                        "No se permite asignar SUPERADMIN.");
            }
            target.setRol(rolNuevo);
        }

        // Username
        if (usernameNuevo != null && !usernameNuevo.trim().isEmpty()) {
            String nuevo = usernameNuevo.trim();

            if (!nuevo.equals(target.getUsername())
                    && usuarioRepository.existsByUsername(nuevo)) {
                throw new IllegalArgumentException(
                        "Ya existe un usuario con ese username.");
            }
            target.setUsername(nuevo);
        }

        // Password (opcional)
        if (passwordNuevaOpcional != null
                && !passwordNuevaOpcional.trim().isEmpty()) {

            if (passwordNuevaOpcional.trim().length() < 4) {
                throw new IllegalArgumentException(
                        "La contraseña debe tener al menos 4 caracteres.");
            }
            target.setPassword(
                    passwordEncoder.encode(passwordNuevaOpcional.trim()));
        }

        return usuarioRepository.save(target);
    }

    /**
     * 🗑️ ELIMINAR USUARIO
     */
    public void eliminarUsuario(Long id) {

        if (!esSuperAdmin()) {
            throw new AccessDeniedException(
                    "Solo SUPERADMIN puede eliminar usuarios.");
        }

        Usuario target = buscarPorId(id);

        if (target.getRol() == Rol.SUPERADMIN) {
            throw new AccessDeniedException(
                    "No se puede eliminar un SUPERADMIN.");
        }

        String actual = usernameActual();
        if (actual != null && actual.equals(target.getUsername())) {
            throw new AccessDeniedException(
                    "No podés eliminar tu propio usuario.");
        }

        usuarioRepository.delete(target);
    }

    /* ======================================================
       HELPERS DE SEGURIDAD
       ====================================================== */

    private String usernameActual() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private boolean esSuperAdmin() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }

    private boolean esAdmin() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SUPERADMIN"));
    }
}
