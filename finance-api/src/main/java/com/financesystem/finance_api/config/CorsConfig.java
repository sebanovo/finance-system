package com.financesystem.finance_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permite CORS en todas las rutas de la API
                        .allowedOrigins("*") // Permite cualquier origen (Angular, Postman, etc.)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*") // Permite todos los encabezados (importante para Auth)
                        .exposedHeaders("Authorization") // Útil si luego envías tokens JWT
                        .allowCredentials(false); // Con "*", esto debe ser false por seguridad del navegador
            }
        };
    }
}