package com.kbcnmu.eventmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS configuration
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("https://event-manager-drh9fp4xt-vishal-sonones-projects.vercel.app")
                        .allowedMethods("GET", "POST", "DELETE", "PUT")
                        .allowedHeaders("*");
            }
        };
    }

    // Static file serving from /uploads/media/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serves files at http://localhost:8080/uploads/media/filename.ext
        registry.addResourceHandler("/uploads/media/**")
                .addResourceLocations("file:uploads/media/");
    }
}
