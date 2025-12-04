package com.medicconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenmrsNameDTO {

    private String givenName;
    private String familyName;
    private Boolean preferred = true;
}
