package com.medicconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenmrsConfig {

    @Value("${openmrs.base-url}")
    private String baseUrl;

    @Value("${openmrs.username}")
    private String username;

    @Value("${openmrs.password}")
    private String password;

    @Value("${openmrs.default-location-uuid}")
    private String defaultLocationUuid;

    @Value("${openmrs.default-identifier-type-uuid}")
    private String defaultIdentifierTypeUuid;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDefaultLocationUuid() {
        return defaultLocationUuid;
    }

    public String getDefaultIdentifierTypeUuid() {
        return defaultIdentifierTypeUuid;
    }
}
