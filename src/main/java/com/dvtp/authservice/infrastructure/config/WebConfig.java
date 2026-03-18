package com.dvtp.authservice.infrastructure.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả API
                .allowedOrigins(
                        "http://localhost:5173", // Link Frontend dưới máy (Vite)
                        "http://localhost:3000", // Link Frontend dưới máy (React cũ)
                        "https://ten-frontend-cua-ong.vercel.app" // Link Frontend sau khi deploy (nếu có)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
