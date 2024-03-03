package com.stc.namada.me.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("localhost:8282", "http://jecbox.com", "https://jecbox.com", "localhost:5000")
                .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}

