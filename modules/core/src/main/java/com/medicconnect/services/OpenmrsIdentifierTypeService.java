package com.medicconnect.services;

import com.medicconnect.api.OpenmrsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenmrsIdentifierTypeService {

    private final OpenmrsApiClient openmrsApiClient;

    public Object getAllIdentifierTypes() throws Exception {
        return openmrsApiClient.get("/patientidentifiertype?v=full");
    }
}
