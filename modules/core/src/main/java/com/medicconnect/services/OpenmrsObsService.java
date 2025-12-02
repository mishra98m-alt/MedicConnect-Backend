package com.medicconnect.services;

import com.medicconnect.api.OpenmrsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenmrsObsService {

    private final OpenmrsApiClient openmrsApiClient;

    public Object getObsByEncounter(String encounterUuid) throws Exception {
        return openmrsApiClient.get("/obs?encounter=" + encounterUuid + "&v=full");
    }

    public Object createObs(Object requestBody) throws Exception {
        return openmrsApiClient.post("/obs", requestBody);
    }
}
