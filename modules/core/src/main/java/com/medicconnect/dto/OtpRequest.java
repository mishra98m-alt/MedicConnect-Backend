package com.medicconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpRequest {

    @NotBlank(message = "Target is required")  // can be email or phone
    private String target;

    @NotBlank(message = "Type is required") // "email" or "phone"
    private String type;

    public OtpRequest() {}

    public OtpRequest(String target, String type) {
        this.target = target;
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
