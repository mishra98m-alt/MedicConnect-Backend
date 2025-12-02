package com.medicconnect.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicconnect.config.OpenmrsConfig;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;

@Component
public class OpenmrsApiClient {
    private static final Logger log = LoggerFactory.getLogger(OpenmrsApiClient.class);
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OpenmrsConfig config;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public OpenmrsApiClient(OpenmrsConfig config) {
        this.config = Objects.requireNonNull(config, "OpenmrsConfig must be provided");
        this.client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .callTimeout(Duration.ofSeconds(60))
                .build();
    }

    private String buildAuthHeader() {
        String creds = config.getUsername() + ":" + config.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }

    private Request.Builder baseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", buildAuthHeader())
                .addHeader("Accept", "application/json");
    }

    private String fullUrl(String endpoint) {
        String base = config.getBaseUrl();
        if (base.endsWith("/") && endpoint.startsWith("/")) {
            return base + endpoint.substring(1);
        } else if (!base.endsWith("/") && !endpoint.startsWith("/")) {
            return base + "/" + endpoint;
        } else {
            return base + endpoint;
        }
    }

    public JsonNode post(String endpoint, Object payload) throws Exception {
        String url = fullUrl(endpoint);
        String bodyJson = mapper.writeValueAsString(payload);

        RequestBody body = RequestBody.create(bodyJson, JSON);
        Request request = baseRequest(url)
                .post(body)
                .build();

        log.debug("[OpenmrsApiClient] POST {} ({} bytes)", url, bodyJson.length());
        try (Response resp = client.newCall(request).execute()) {
            int code = resp.code();
            String respBody = resp.body() != null ? resp.body().string() : "";
            if (code < 200 || code >= 300) {
                log.warn("[OpenmrsApiClient] Non-2xx response {}: {}", code, respBody);
                throw new RuntimeException("OpenMRS POST failed: HTTP " + code + " - " + respBody);
            }
            if (respBody.isEmpty()) return mapper.createObjectNode();
            return mapper.readTree(respBody);
        } catch (Exception e) {
            log.error("[OpenmrsApiClient] POST failed to {} : {}", url, e.getMessage());
            throw e;
        }
    }

    public JsonNode get(String endpoint) throws Exception {
        String url = fullUrl(endpoint);
        Request request = baseRequest(url).get().build();
        log.debug("[OpenmrsApiClient] GET {}", url);
        try (Response resp = client.newCall(request).execute()) {
            int code = resp.code();
            String respBody = resp.body() != null ? resp.body().string() : "";
            if (code < 200 || code >= 300) {
                log.warn("[OpenmrsApiClient] GET non-2xx {}: {}", code, respBody);
                throw new RuntimeException("OpenMRS GET failed: HTTP " + code + " - " + respBody);
            }
            if (respBody.isEmpty()) return mapper.createObjectNode();
            return mapper.readTree(respBody);
        }
    }
}
