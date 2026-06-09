package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    @Value("${app.backend.url}")
    private String backendUrl;
    
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;
    
    public String getFrontendUrl() {
        return frontendUrl;
    }
    
    public String getBackendUrl() {
        return backendUrl;
    }

    public String getAllowedOrigins(){
        return allowedOrigins;
    }
    
    public String getApiBaseUrl() {
        return backendUrl + "/api";
    }
}