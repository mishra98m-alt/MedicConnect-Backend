package com.medicconnect.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.medicconnect.api.OpenmrsApiClient;
import com.medicconnect.config.OpenmrsConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OpenmrsPatientService {

    private final OpenmrsApiClient api;
    private final OpenmrsConfig config;

    public OpenmrsPatientService(OpenmrsApiClient api, OpenmrsConfig config) {
        this.api = api;
        this.config = config;
    }

    /**
     * Create a new patient in OpenMRS
     */
    public JsonNode createPatient(String firstName, String lastName, String gender, String birthdate) throws Exception {

        // -----------------------------
        // Name object
        // -----------------------------
        Map<String, Object> name = new HashMap<>();
        name.put("givenName", firstName);
        name.put("familyName", lastName);

        List<Map<String, Object>> namesList = List.of(name);

        // -----------------------------
        // Person section
        // -----------------------------
        Map<String, Object> person = new HashMap<>();
        person.put("gender", gender);
        person.put("birthdate", birthdate);
        person.put("names", namesList);

        // -----------------------------
        // Identifier (TEMPORARY generator)
        // -----------------------------
        Map<String, Object> identifier = new HashMap<>();
        identifier.put("identifier", generateIdentifier());
        identifier.put("identifierType", config.getDefaultIdentifierTypeUuid());
        identifier.put("location", config.getDefaultLocationUuid());
        identifier.put("preferred", true);

        List<Map<String, Object>> identifiersList = List.of(identifier);

        // -----------------------------
        // Final OpenMRS payload
        // -----------------------------
        Map<String, Object> payload = new HashMap<>();
        payload.put("person", person);
        payload.put("identifiers", identifiersList);

        return api.post("/patient", payload);
    }


    /**
     * TEMP identifier until we switch to OpenMRS IDGEN module
     */
    private String generateIdentifier() {
        // Example: 8-digit alphanumeric temporary ID
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
