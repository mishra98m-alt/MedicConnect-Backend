package com.medicconnect.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenmrsPatientPayload {

    private String gender;               // "M" or "F"
    private String birthdate;            // e.g., "1990-12-03"
    private boolean birthdateEstimated = false;

    private List<OpenmrsNameDTO> names;
    private List<OpenmrsAddressDTO> addresses;
    private List<OpenmrsIdentifierDTO> identifiers;
}
