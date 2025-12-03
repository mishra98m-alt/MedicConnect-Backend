package com.medicconnect.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.medicconnect.api.OpenmrsApiClient;
import com.medicconnect.config.OpenmrsConfig;
import com.medicconnect.dto.OpenmrsPatientPayload;
import org.springframework.stereotype.Service;

@Service
public class OpenmrsPatientService {

    private final OpenmrsApiClient api;
    private final OpenmrsConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenmrsPatientService(OpenmrsApiClient api, OpenmrsConfig config) {
        this.api = api;
        this.config = config;
    }

    public String createPatient(OpenmrsPatientPayload payload) throws Exception {

        // -----------------------------------------------------
        // Build PERSON JSON (OpenMRS format)
        // -----------------------------------------------------
        ObjectNode person = mapper.createObjectNode();
        person.put("gender", payload.getGender());
        person.put("birthdate", payload.getBirthdate());
        person.put("birthdateEstimated", payload.isBirthdateEstimated());

        // Names --> [{"givenName": "...", "familyName": "..."}]
        person.set("names", mapper.valueToTree(payload.getNames()));

        // Addresses --> [{"address1": "...", "cityVillage": "..."}]
        person.set("addresses", mapper.valueToTree(payload.getAddresses()));

        // -----------------------------------------------------
        // IDENTIFIERS LIST — ensure preferred + correct fields
        // -----------------------------------------------------
        JsonNode identifiers = mapper.valueToTree(payload.getIdentifiers());

        // Add "preferred" if missing
        identifiers.forEach(id -> {
            if (!id.has("preferred")) {
                ((ObjectNode) id).put("preferred", true);
            }
        });

        // -----------------------------------------------------
        // FINAL PATIENT OBJECT
        // -----------------------------------------------------
        ObjectNode request = mapper.createObjectNode();
        request.set("person", person);
        request.set("identifiers", identifiers);

        // -----------------------------------------------------
        // SEND TO OPENMRS (correct endpoint!)
        // -----------------------------------------------------
        JsonNode response = api.post("/patient/", request);

        if (response.has("uuid"))
            return response.get("uuid").asText();

        throw new RuntimeException("OpenMRS did not return patient UUID: " + response);
    }
}
