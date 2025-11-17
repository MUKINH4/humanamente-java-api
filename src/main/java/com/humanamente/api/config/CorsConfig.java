package com.humanamente.api.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        
        // Permite origens específicas
        config.setAllowedOriginPatterns(List.of("*"));
        
        // Permite todos os headers, incluindo Authorization
        config.setAllowedHeaders(List.of("*"));
        
        // Permite todos os métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // Expõe headers de resposta (importante para Authorization)
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));
        
        // Cache de preflight por 1 hora
        config.setMaxAge(3600L);
        
        // Aplica a configuração para todos os endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
        
    
}
