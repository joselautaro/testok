package com.testok.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.testok.entities.Rol;
import com.testok.entities.Usuario;
import com.testok.services.UsuarioService;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ✅ LISTAR (FILTRADO SEGÚN ROL)
    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "ok", required = false) String ok,
                         @RequestParam(value = "err", required = false) String err) {

        if (esSuperAdmin()) {
            // SUPERADMIN ve ADMIN + USER
            model.addAttribute(
                    "usuarios",
                    usuarioService.listarAdminsYClientesParaSuperadmin()
            );
        } else {
            // ADMIN ve solo USER
            model.addAttribute(
                    "usuarios",
                    usuarioService.listarSoloClientes()
            );
        }

        model.addAttribute("ok", ok);
        model.addAttribute("err", err);
        model.addAttribute("esSuperAdmin", esSuperAdmin());

        return "admin-usuarios";
    }

    // ✅ FORM NUEVO
    @GetMapping("/nuevo")
    public String formNuevo(Model model) {

        model.addAttribute("esSuperAdmin", esSuperAdmin());
        model.addAttribute("roles", rolesPermitidosParaFormulario());

        return "admin-usuario-form";
    }

    // ✅ CREAR
    @PostMapping
    public String crear(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) Rol rol,
                        RedirectAttributes ra) {

        try {
            if (!esSuperAdmin()) {
                rol = Rol.USER;
            }

            usuarioService.crearUsuario(username, password, rol);
            ra.addAttribute("ok", "Usuario creado correctamente");
            return "redirect:/admin/usuarios";

        } catch (IllegalArgumentException ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }

    // ✅ FORM EDITAR
    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Usuario u = usuarioService.buscarPorId(id);

            model.addAttribute("usuario", u);
            model.addAttribute("esSuperAdmin", esSuperAdmin());
            model.addAttribute("roles", rolesPermitidosParaFormulario());

            return "admin-usuario-edit";

        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    // ✅ GUARDAR EDICIÓN
    @PostMapping("/{id}/editar")
    public String guardarEdicion(@PathVariable Long id,
                                 @RequestParam(required = false) String username,
                                 @RequestParam(required = false) String password,
                                 @RequestParam(required = false) Rol rol,
                                 RedirectAttributes ra) {
        try {
            if (!esSuperAdmin()) {
                rol = null;
            }

            usuarioService.editarUsuario(id, username, password, rol);

            ra.addAttribute("ok", "Usuario actualizado correctamente");
            return "redirect:/admin/usuarios";

        } catch (AccessDeniedException | IllegalArgumentException ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";

        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    // ✅ ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuarioService.eliminarUsuario(id);
            ra.addAttribute("ok", "Usuario eliminado correctamente");
            return "redirect:/admin/usuarios";

        } catch (AccessDeniedException ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios";

        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private boolean esSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SUPERADMIN".equals(a.getAuthority()));
    }

    private List<Rol> rolesPermitidosParaFormulario() {
        if (esSuperAdmin()) {
            return Arrays.asList(Rol.USER, Rol.ADMIN);
        }
        return List.of(Rol.USER);
    }
}
