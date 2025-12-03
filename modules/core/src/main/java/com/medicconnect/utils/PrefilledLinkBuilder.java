package com.medicconnect.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrefilledLinkBuilder {

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    public String buildLink(String tokenOrUrl) {

        if (tokenOrUrl.startsWith("http")) {
            return tokenOrUrl;
        }

        String base = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1)
                : frontendBaseUrl;

        return base + "/#/register?token=" + tokenOrUrl;
    }
}
