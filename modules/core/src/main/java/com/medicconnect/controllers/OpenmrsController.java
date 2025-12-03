package com.medicconnect.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicconnect.dto.OpenmrsPatientPayload;
import com.medicconnect.services.OpenmrsPatientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openmrs")
public class OpenmrsController {

    private final OpenmrsPatientService patientService;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenmrsController(OpenmrsPatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/patients")
    public String createPatient(@RequestBody JsonNode node) throws Exception {

        // Extract "person" object fields into payload
        JsonNode person = node.get("person");

        OpenmrsPatientPayload payload = new OpenmrsPatientPayload();
        payload.setGender(person.get("gender").asText());
        payload.setBirthdate(person.get("birthdate").asText());
        payload.setBirthdateEstimated(person.get("birthdateEstimated").asBoolean());

        payload.setNames(mapper.convertValue(person.get("names"), payload.getNames().getClass()));
        payload.setAddresses(mapper.convertValue(person.get("addresses"), payload.getAddresses().getClass()));

        payload.setIdentifiers(
                mapper.convertValue(node.get("identifiers"), payload.getIdentifiers().getClass())
        );

        return patientService.createPatient(payload);
    }
}
