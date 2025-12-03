package com.medicconnect.dto;

import lombok.Data;

@Data
public class OpenmrsNameDTO {
    private String givenName;
    private String familyName;

    // Optional but supported by OpenMRS
    private Boolean preferred = true;
}
