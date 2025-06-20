package com.pageon.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대해
        registry.addMapping("/**")

            // Origin이 http://localhost:3000에 대해
            .allowedOrigins("http://localhost:3000")
            // GET, POST, PUT, PATCH, DELETE, OPTIONS 메서드를 허용한다.
            .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS");
    }
}
