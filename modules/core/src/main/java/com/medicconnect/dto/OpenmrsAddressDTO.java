package com.medicconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)   // REMOVE NULL FIELDS IN JSON
public class OpenmrsAddressDTO {

    private String address1;
    private String address2;          // optional
    private String cityVillage;
    private String stateProvince;     // optional
    private String country;           // optional
    private String postalCode;        // optional

    private Boolean preferred = true;
}
