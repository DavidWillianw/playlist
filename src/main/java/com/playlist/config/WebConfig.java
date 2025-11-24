package com.playlist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Libera para todas as rotas
                .allowedOrigins("*") // Libera para qualquer Front-end 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"); // Libera os métodos
    }
}