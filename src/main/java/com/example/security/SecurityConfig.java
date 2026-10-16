package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desactiva CSRF para permitir pruebas libres en Postman (POST, PUT, DELETE)
            .authorizeRequests()
            .anyRequest().permitAll(); // Permite el acceso libre a todas las rutas sin pedir contraseñas ni tokens
        
        return http.build();
    }

}
