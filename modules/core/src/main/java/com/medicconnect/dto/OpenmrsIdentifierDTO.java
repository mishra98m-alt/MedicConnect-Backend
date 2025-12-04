package com.medicconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenmrsIdentifierDTO {

    private String identifier;
    private String identifierType;
    private String location;
    private Boolean preferred = true;
}
