package com.medicconnect.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.medicconnect.api.OpenmrsApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openmrs")
public class OpenmrsTestController {

    private final OpenmrsApiClient openmrsApiClient;

    public OpenmrsTestController(OpenmrsApiClient openmrsApiClient) {
        this.openmrsApiClient = openmrsApiClient;
    }

    @GetMapping("/test-login")
    public ResponseEntity<?> testLogin() {
        try {
            JsonNode result = openmrsApiClient.testSession();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("OpenMRS connection failed: " + e.getMessage());
        }
    }
}
