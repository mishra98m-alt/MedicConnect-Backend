package com.medicconnect.controllers;

import com.medicconnect.models.Person;
import com.medicconnect.models.Status;
import com.medicconnect.services.PersonService;
import com.medicconnect.services.VerificationService;
import com.medicconnect.utils.AuthTokenUtil;
import com.medicconnect.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PersonService personService;
    private final VerificationService verificationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthTokenUtil authTokenUtil;

    @Autowired
    public AuthController(PersonService personService,
                          VerificationService verificationService,
                          BCryptPasswordEncoder passwordEncoder,
                          AuthTokenUtil authTokenUtil) {
        this.personService = personService;
        this.verificationService = verificationService;
        this.passwordEncoder = passwordEncoder;
        this.authTokenUtil = authTokenUtil;
    }

    // ---------------- Password Login ----------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String password = request.get("password");

        if (identifier == null || identifier.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(
                    "Identifier and password are required", Map.of("userExists", 0)));
        }

        try {
            Person person = personService.findByEmailOrUserId(identifier).orElse(null);

            if (person == null) {
                return ResponseEntity.ok(ResponseUtils.error("User not found", Map.of("userExists", 0)));
            }

            if (person.getStatus() != Status.APPROVED) {
                return ResponseEntity.badRequest().body(ResponseUtils.error(
                        "User is not approved by the main admin yet", Map.of("userExists", 1)));
            }

            if (!passwordEncoder.matches(password, person.getPassword())) {
                return ResponseEntity.badRequest().body(ResponseUtils.error(
                        "Invalid credentials", Map.of("userExists", 1)));
            }

            // ✅ Generate JWT token for password-based login
            String token = authTokenUtil.generateToken(Map.of(
                    "userId", person.getUserId(),
                    "email", person.getEmail(),
                    "roles", person.getRoles()
            ));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userExists", 1);
            userInfo.put("token", token);
            userInfo.put("userId", person.getUserId());
            userInfo.put("name", person.getName());
            userInfo.put("email", person.getEmail());
            userInfo.put("mobile", person.getMobile());
            userInfo.put("orgId", person.getOrganization() != null ? person.getOrganization().getOrgId() : null);
            userInfo.put("roles", person.getRoles());

            return ResponseEntity.ok(ResponseUtils.success("Login successful", userInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), Map.of("userExists", 0)));
        }
    }

    // ---------------- Send OTP ----------------
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String type = request.get("type"); // login or verification

        if (identifier == null || identifier.isBlank() || type == null || type.isBlank()) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(
                    "Identifier and type are required", Map.of("userExists", 0)));
        }

        try {
            Person person = personService.findByEmailOrUserId(identifier).orElse(null);

            if (person == null) {
                return ResponseEntity.ok(ResponseUtils.error("User not found", Map.of("userExists", 0)));
            }

            if ("login".equalsIgnoreCase(type) && person.getStatus() != Status.APPROVED) {
                return ResponseEntity.badRequest().body(ResponseUtils.error(
                        "User is not approved by the main admin yet", Map.of("userExists", 1)));
            }

            verificationService.sendOtp(person.getEmail(), type);

            Map<String, Object> data = new HashMap<>();
            data.put("userExists", 1);
            data.put("identifier", identifier);
            data.put("type", type);

            return ResponseEntity.ok(ResponseUtils.success("OTP sent successfully", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), Map.of("userExists", 0)));
        }
    }

    // ---------------- Verify OTP ----------------
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String otp = request.get("otp");

        if (identifier == null || identifier.isBlank() || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(
                    "Identifier and OTP are required", Map.of("userExists", 0)));
        }

        try {
            Person person = personService.findByEmailOrUserId(identifier).orElse(null);

            if (person == null) {
                return ResponseEntity.ok(ResponseUtils.error("User not found", Map.of("userExists", 0)));
            }

            if (person.getStatus() != Status.APPROVED) {
                return ResponseEntity.badRequest().body(ResponseUtils.error(
                        "User is not approved by the main admin yet", Map.of("userExists", 1)));
            }

            boolean verified = verificationService.verifyOtp(person.getEmail(), otp);
            if (!verified) {
                return ResponseEntity.badRequest().body(ResponseUtils.error(
                        "Invalid OTP", Map.of("userExists", 1)));
            }

            // ✅ Generate JWT token for OTP-based login
            String token = authTokenUtil.generateToken(Map.of(
                    "userId", person.getUserId(),
                    "email", person.getEmail(),
                    "roles", person.getRoles()
            ));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userExists", 1);
            userInfo.put("token", token);
            userInfo.put("orgId", person.getOrganization() != null ? person.getOrganization().getOrgId() : null);
            userInfo.put("userId", person.getUserId());
            userInfo.put("email", person.getEmail());
            userInfo.put("name", person.getName());
            userInfo.put("roles", person.getRoles());

            return ResponseEntity.ok(ResponseUtils.success("OTP verified successfully", userInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), Map.of("userExists", 0)));
        }
    }
}
