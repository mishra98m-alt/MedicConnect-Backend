package com.medicconnect.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final EmailService emailService;

    @Autowired
    public VerificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    // -----------------------------
    // Send OTP (login, email verification, phone)
    // -----------------------------
    public void sendOtp(String target, String type) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStore.put(target, otp);

        try {
            if ("phone".equalsIgnoreCase(type)) {
                // Log phone OTP to console
                System.out.println("[VerificationService] OTP for phone " + target + ": " + otp);
            } else if ("login".equalsIgnoreCase(type) || "email".equalsIgnoreCase(type) || "verification".equalsIgnoreCase(type)) {
                String htmlBody;
                if ("login".equalsIgnoreCase(type)) {
                    htmlBody = emailService.generateLoginOtpEmail(target, otp);
                } else {
                    htmlBody = emailService.generateEmailVerificationOtpEmail(target, otp);
                }
                emailService.sendEmail(target, "Medic-connect OTP", htmlBody);
                System.out.println("[VerificationService] OTP sent to " + target + " (" + type + ")");
            } else {
                throw new IllegalArgumentException("Invalid OTP type: " + type);
            }
        } catch (Exception e) {
            System.err.println("[VerificationService] Failed to send OTP to " + target + ": " + e.getMessage());
        }
    }

    // -----------------------------
    // Verify OTP
    // -----------------------------
    public boolean verifyOtp(String target, String otp) {
        String correctOtp = otpStore.get(target);
        if (correctOtp != null && correctOtp.equals(otp)) {
            otpStore.remove(target);
            return true;
        }
        return false;
    }
}
