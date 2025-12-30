package com.testok.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/clientes", "/error", "/403", "/image/**", "/css/**", "/js/**").permitAll()

                // ✅ SOLO SUPERADMIN
                .requestMatchers("/superadmin/**").hasRole("SUPERADMIN")

                // ✅ ADMIN y SUPERADMIN
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")

                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
        .loginPage("/login")              // usa tu template login.html
        .defaultSuccessUrl("/admin/clientes", true)     // a dónde va si loguea OK
        .failureUrl("/login?error=true")  // para mostrar mensaje
        .permitAll()
);
        http.logout(logout -> logout
        .logoutSuccessUrl("/login?logout=true")
        .permitAll()
);

        http.exceptionHandling(e -> e.accessDeniedPage("/403"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
