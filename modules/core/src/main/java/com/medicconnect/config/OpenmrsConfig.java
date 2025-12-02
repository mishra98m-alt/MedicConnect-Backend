package com.medicconnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openmrs")
public class OpenmrsConfig {
    /**
     * Example properties that will be read from:
     * - application-openmrs.properties (when spring profile openmrs active)
     * - or env vars like OPENMRS_BASE_URL, OPENMRS_USERNAME, etc (Spring relaxed binding)
     */
    private String baseUrl;
    private String username;
    private String password;
    private String defaultLocationUuid;
    private String defaultIdentifierTypeUuid;
    // getters & setters

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDefaultLocationUuid() { return defaultLocationUuid; }
    public void setDefaultLocationUuid(String defaultLocationUuid) { this.defaultLocationUuid = defaultLocationUuid; }

    public String getDefaultIdentifierTypeUuid() { return defaultIdentifierTypeUuid; }
    public void setDefaultIdentifierTypeUuid(String defaultIdentifierTypeUuid) { this.defaultIdentifierTypeUuid = defaultIdentifierTypeUuid; }
}
