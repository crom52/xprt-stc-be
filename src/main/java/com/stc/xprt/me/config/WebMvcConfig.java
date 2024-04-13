package com.stc.xprt.me.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("localhost:8282", "http://jecbox.com", "https://jecbox.com", "localhost:5000", "localhost:5000", "https://namada-me-be-0c5a9f264e40.herokuapp.com", "http://namada-me-be-0c5a9f264e40.herokuapp.com", "23.88.126.45:8080", "23.88.126.45")
                .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}

