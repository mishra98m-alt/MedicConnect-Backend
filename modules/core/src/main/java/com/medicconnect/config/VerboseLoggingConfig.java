package com.medicconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class VerboseLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);       // IP, session, etc.
        filter.setIncludeQueryString(true);      // ?token=abc
        filter.setIncludePayload(true);          // JSON request body
        filter.setMaxPayloadLength(20000);       // Increase if needed
        filter.setIncludeHeaders(false);         // Disable header spam
        return filter;
    }
}
