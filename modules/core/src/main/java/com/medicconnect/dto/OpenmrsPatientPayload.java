package com.medicconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenmrsPatientPayload {

    private String gender;
    private String birthdate;
    private boolean birthdateEstimated = false;

    private List<OpenmrsNameDTO> names;
    private List<OpenmrsAddressDTO> addresses;
    private List<OpenmrsIdentifierDTO> identifiers;
}
