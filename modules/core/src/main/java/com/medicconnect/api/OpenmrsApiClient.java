package com.medicconnect.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicconnect.config.OpenmrsConfig;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class OpenmrsApiClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OpenmrsConfig config;

    public OpenmrsApiClient(OpenmrsConfig config) {
        this.config = config;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .retryOnConnectionFailure(true)
                .build();
    }

    // =========================================================
    //  Helpers
    // =========================================================

    private String buildAuthHeader() {
        String creds = config.getUsername() + ":" + config.getPassword();
        return "Basic " + Base64.getEncoder()
                .encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }

    private Request.Builder baseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", buildAuthHeader())
                .addHeader("Content-Type", "application/json");
    }

    private JsonNode execute(Request request) throws Exception {
        try (Response resp = client.newCall(request).execute()) {

            if (!resp.isSuccessful()) {
                String errorBody = resp.body() != null ? resp.body().string() : "";
                throw new RuntimeException(
                        "OpenMRS API Error (" + resp.code() + "): " + errorBody
                );
            }

            String json = resp.body() != null ? resp.body().string() : "{}";
            return mapper.readTree(json);
        }
    }

    private String url(String endpoint) {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        return config.getBaseUrl() + endpoint;
    }

    // =========================================================
    //  Generic GET / POST
    // =========================================================

    public JsonNode get(String endpoint) throws Exception {
        Request request = baseRequest(url(endpoint))
                .get()
                .build();

        return execute(request);
    }

    public JsonNode post(String endpoint, Object payload) throws Exception {
        RequestBody body = RequestBody.create(
                mapper.writeValueAsString(payload),
                MediaType.parse("application/json")
        );

        Request request = baseRequest(url(endpoint))
                .post(body)
                .build();

        return execute(request);
    }

    // =========================================================
    //  Test connection to OpenMRS
    // =========================================================

    public JsonNode testSession() throws Exception {
        return get("/session");
    }

    // =========================================================
    //  Wrapper Methods (Optional)
    // =========================================================

    public JsonNode getPatient(String uuid) throws Exception {
        return get("/patient/" + uuid);
    }

    public JsonNode searchPatient(String query) throws Exception {
        return get("/patient?q=" + query);
    }

    public JsonNode createPatient(Object payload) throws Exception {
        return post("/patient", payload);
    }
}
