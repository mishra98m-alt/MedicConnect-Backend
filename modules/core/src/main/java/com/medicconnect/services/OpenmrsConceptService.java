package com.medicconnect.services;

import com.medicconnect.api.OpenmrsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenmrsConceptService {

    private final OpenmrsApiClient openmrsApiClient;

    /**
     * Search concepts in OpenMRS
     */
    public Object searchConcept(String query) throws Exception {
        return openmrsApiClient.get("/concept?q=" + query + "&v=full");
    }

    /**
     * Fetch concept by UUID
     */
    public Object getConceptByUuid(String uuid) throws Exception {
        return openmrsApiClient.get("/concept/" + uuid + "?v=full");
    }
}
