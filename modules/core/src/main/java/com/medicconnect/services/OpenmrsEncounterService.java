package com.medicconnect.services;

import com.medicconnect.api.OpenmrsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenmrsEncounterService {

    private final OpenmrsApiClient openmrsApiClient;

    /**
     * Fetch all encounter types from OpenMRS
     */
    public Object getAllEncounterTypes() {
        try {
            return openmrsApiClient.get("/encountertype?v=full");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch encounter types: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new OpenMRS encounter
     */
    public Object createEncounter(Object requestBody) {
        try {
            return openmrsApiClient.post("/encounter", requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create encounter: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch all encounters for a patient
     */
    public Object getEncountersForPatient(String patientUuid) {
        try {
            return openmrsApiClient.get("/encounter?patient=" + patientUuid + "&v=full");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get encounters for patient: " + e.getMessage(), e);
        }
    }
}
