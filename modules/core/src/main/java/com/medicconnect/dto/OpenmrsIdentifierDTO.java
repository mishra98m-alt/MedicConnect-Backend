package com.medicconnect.dto;

import lombok.Data;

@Data
public class OpenmrsIdentifierDTO {

    private String identifier;        // REQUIRED (actual patient number)
    private String identifierType;    // UUID of identifier type
    private String location;          // UUID of registration location
    private Boolean preferred = true;
}
