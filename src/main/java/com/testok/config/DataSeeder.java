package com.testok.config;

import com.testok.entities.Rol;
import com.testok.entities.Usuario;
import com.testok.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initUsers(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (!repo.existsByUsername("admin")) {
                repo.save(new Usuario("admin", encoder.encode("admin123"), Rol.ADMIN));
            }
            if (!repo.existsByUsername("user")) {
                repo.save(new Usuario("user", encoder.encode("user123"), Rol.USER));
            }
        };
    }
}
