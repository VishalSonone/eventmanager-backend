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
                        .allowedOrigins(
                                "http://localhost:5173",
                                "https://event-manager-drh9fp4xt-vishal-sonones-projects.vercel.app",
<<<<<<< HEAD
                                "https://eventmanagerment-deploy.onrender.com"
=======
                            "https://eventmanagerment-deploy.onrender.com"
>>>>>>> be27710069affd28cff02fd3588471910a4b48fc
                        )
                        .allowedMethods("GET", "POST", "DELETE", "PUT")
                        .allowedHeaders("*");
            }
        };
    }

    // Static file serving
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // For media files
        registry.addResourceHandler("/uploads/media/**")
                .addResourceLocations("file:uploads/media/");

        // ✅ For bug screenshots
        registry.addResourceHandler("/uploads/bugs/**")
                .addResourceLocations("file:uploads/bugs/");
    }
}
