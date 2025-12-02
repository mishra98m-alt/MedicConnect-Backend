package com.medicconnect.dto;

import lombok.Data;

@Data
public class OpenmrsPatientRequest {
    private String firstName;
    private String lastName;
    private String gender;       // “M” or “F”
    private String birthdate;    // yyyy-MM-dd
}
