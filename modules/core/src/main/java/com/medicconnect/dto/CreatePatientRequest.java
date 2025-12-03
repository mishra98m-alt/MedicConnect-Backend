package com.medicconnect.dto;

import lombok.Data;

@Data
public class CreatePatientRequest {
    private String givenName;
    private String familyName;
    private String gender;
    private String birthdate;

    private String phoneNumber;
    private String nationalId;
}
