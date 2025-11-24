package com.playlist.config;

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
            .csrf(csrf -> csrf.disable()) // Desabilita proteção contra ataques (necessário para o POST funcionar livremente)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Qualquer um pode acessar qualquer URL sem login
            );
        
        return http.build();
    }
}