package com.pintoss.auth.common.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "HEAD")
            .allowedHeaders("*")
            .exposedHeaders("Set-Cookie")
            .allowCredentials(true);
    }
}
