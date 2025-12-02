package com.medicconnect.services;

import com.medicconnect.api.OpenmrsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenmrsLocationService {

    private final OpenmrsApiClient openmrsApiClient;

    /**
     * Fetch all OpenMRS locations
     */
    public Object getAllLocations() {
        try {
            return openmrsApiClient.get("/location?v=full");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch OpenMRS locations: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch a single OpenMRS location by UUID
     */
    public Object getLocationByUuid(String uuid) {
        try {
            return openmrsApiClient.get("/location/" + uuid + "?v=full");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch location: " + e.getMessage(), e);
        }
    }
}
