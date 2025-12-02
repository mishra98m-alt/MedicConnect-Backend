package com.medicconnect.controllers;

import com.medicconnect.services.VerificationService;
import com.medicconnect.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> payload) {
        String target = payload.get("target");
        String type = payload.get("type");

        if (target == null || type == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Missing target or type", null));
        }

        verificationService.sendOtp(target, type);
        return ResponseEntity.ok(ResponseUtils.success("OTP sent successfully", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> payload) {
        String target = payload.get("target");
        String otp = payload.get("otp");

        if (target == null || otp == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Missing target or OTP", null));
        }

        boolean verified = verificationService.verifyOtp(target, otp);
        if (verified) {
            return ResponseEntity.ok(ResponseUtils.success("Verified successfully", null));
        } else {
            return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid OTP", null));
        }
    }
}
