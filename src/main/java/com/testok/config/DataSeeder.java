package com.testok.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.testok.entities.Rol;
import com.testok.entities.Usuario;
import com.testok.repositories.UsuarioRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initUsers(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {

            // ✅ SUPERADMIN (sin guion bajo)
            if (!repo.existsByUsername("superadmin")) {
                Usuario sa = new Usuario();
                sa.setUsername("superadmin");
                sa.setPassword(encoder.encode("superadmin123"));
                sa.setRol(Rol.SUPERADMIN);
                repo.save(sa);
            }

            // ✅ ADMIN
            if (!repo.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRol(Rol.ADMIN);
                repo.save(admin);
            }
        };
    }
}
