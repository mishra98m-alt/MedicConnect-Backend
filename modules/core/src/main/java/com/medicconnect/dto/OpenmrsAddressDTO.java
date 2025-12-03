package com.medicconnect.dto;

import lombok.Data;

@Data
public class OpenmrsAddressDTO {

    private String address1;
    private String address2;          // optional but OpenMRS supports it
    private String cityVillage;
    private String stateProvince;
    private String country;
    private String postalCode;        // optional but valid

    private Boolean preferred = true; // recommended
}
