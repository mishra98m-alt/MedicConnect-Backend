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

@Component
public class OpenmrsApiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenmrsApiClient.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OpenmrsConfig config;

    public OpenmrsApiClient(OpenmrsConfig config) {
        this.config = config;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(20))
                .writeTimeout(Duration.ofSeconds(20))
                .retryOnConnectionFailure(true)
                .build();
    }

    // -------------------------------------------------------------
    // AUTH HEADER
    // -------------------------------------------------------------
    private String authHeader() {
        String creds = config.getUsername() + ":" + config.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }

    // -------------------------------------------------------------
    // NORMALIZE OPENMRS URL
    // Ensures correct format: baseUrl + /endpoint/
    // -------------------------------------------------------------
    private String normalizeUrl(String endpoint) {
        String base = config.getBaseUrl();

        if (base.endsWith("/"))
            base = base.substring(0, base.length() - 1);

        if (!endpoint.startsWith("/"))
            endpoint = "/" + endpoint;

        if (!endpoint.endsWith("/"))
            endpoint = endpoint + "/";

        return base + endpoint;
    }

    // -------------------------------------------------------------
    // BUILD REQUEST WITH REQUIRED HEADERS
    // -------------------------------------------------------------
    private Request buildRequest(String url, RequestBody body, String method) {

        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authHeader())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");  // REQUIRED FOR OPENMRS

        switch (method) {
            case "POST" -> builder.post(body);
            case "GET" -> builder.get();
            case "PUT" -> builder.put(body);
            case "DELETE" -> builder.delete(body);
        }

        return builder.build();
    }

    // -------------------------------------------------------------
    // EXECUTE REQUEST
    // -------------------------------------------------------------
    private JsonNode execute(Request request, String url) throws Exception {
        try (Response resp = client.newCall(request).execute()) {

            if (!resp.isSuccessful()) {
                String errorBody = resp.body() != null ? resp.body().string() : "NO_ERROR_BODY";

                log.error("❌ OpenMRS API Error\nURL: {}\nStatus: {}\nResponse: {}",
                        url, resp.code(), errorBody);

                throw new RuntimeException("OpenMRS API failed: HTTP " + resp.code());
            }

            String result = resp.body() != null ? resp.body().string() : "{}";
            return mapper.readTree(result);
        }
    }

    // -------------------------------------------------------------
    // PUBLIC OPENMRS API METHODS
    // -------------------------------------------------------------
    public JsonNode get(String endpoint) throws Exception {
        String url = normalizeUrl(endpoint);
        Request req = buildRequest(url, null, "GET");
        return execute(req, url);
    }

    public JsonNode post(String endpoint, Object payload) throws Exception {
        String url = normalizeUrl(endpoint);
        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(payload),
                MediaType.parse("application/json")
        );
        Request req = buildRequest(url, body, "POST");
        return execute(req, url);
    }

    public JsonNode put(String endpoint, Object payload) throws Exception {
        String url = normalizeUrl(endpoint);
        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(payload),
                MediaType.parse("application/json")
        );
        Request req = buildRequest(url, body, "PUT");
        return execute(req, url);
    }

    public JsonNode delete(String endpoint) throws Exception {
        String url = normalizeUrl(endpoint);
        Request req = buildRequest(url, RequestBody.create("", null), "DELETE");
        return execute(req, url);
    }

    // -------------------------------------------------------------
    // SESSION CHECK
    // -------------------------------------------------------------
    public JsonNode getSession() throws Exception {
        return get("/session");
    }

    public boolean checkLogin() {
        try {
            JsonNode node = get("/session");
            return node.get("authenticated").asBoolean();
        } catch (Exception e) {
            log.error("OpenMRS session check failed: {}", e.getMessage());
            return false;
        }
    }
}
