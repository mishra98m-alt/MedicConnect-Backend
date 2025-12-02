package com.medicconnect.dto;

import jakarta.validation.constraints.NotBlank;

public class OtpVerifyRequest {

    @NotBlank(message = "Target is required")
    private String target;

    @NotBlank(message = "OTP is required")
    private String otp;

    public OtpVerifyRequest() {}

    public OtpVerifyRequest(String target, String otp) {
        this.target = target;
        this.otp = otp;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
