package com.testok.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testok.entities.Rol;
import com.testok.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    // ✅ Listar solo clientes (USER)
    List<Usuario> findAllByRol(Rol rol);

    // ✅ Listar por varios roles (ej: USER + ADMIN)
    List<Usuario> findAllByRolIn(List<Rol> roles);
}
