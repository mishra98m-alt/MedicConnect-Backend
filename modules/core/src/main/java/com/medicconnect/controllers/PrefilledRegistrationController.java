package com.medicconnect.controllers;

import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import com.medicconnect.models.PrefilledUserData;
import com.medicconnect.services.*;
import com.medicconnect.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 🌐 PrefilledRegistrationController
 * ----------------------------------
 * Handles prefilled registration:
 * 1️⃣ Fetch prefilled data
 * 2️⃣ Complete registration
 */
@RestController
@RequestMapping("/api/prefilled")
public class PrefilledRegistrationController {

    private final PrefilledTokenService tokenService;
    private final PrefilledUserDataService userDataService;
    private final OrganizationService organizationService;
    private final PersonService personService;
    private final EmailService emailService;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    public PrefilledRegistrationController(
            PrefilledTokenService tokenService,
            PrefilledUserDataService userDataService,
            OrganizationService organizationService,
            PersonService personService,
            EmailService emailService
    ) {
        this.tokenService = tokenService;
        this.userDataService = userDataService;
        this.organizationService = organizationService;
        this.personService = personService;
        this.emailService = emailService;
    }

    // -------------------------------------------------------------------------
    // 📄 Step 1: Fetch Prefilled Data
    // -------------------------------------------------------------------------
    @GetMapping(value = "/form", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getPrefilledData(@RequestParam String token) {
        try {
            System.out.println("[PrefilledController] Fetch request for token: " + token);

            if (!tokenService.isTokenValid(token)) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtils.error("Invalid or expired registration link."));
            }

            // ❗ Repository returns PrefilledUserData, not Optional
            PrefilledUserData data = userDataService.getByToken(token);
            if (data == null) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtils.error("No prefilled data found for this link."));
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("orgId", data.getOrgId());
            payload.put("name", data.getName());
            payload.put("email", data.getEmail());
            payload.put("mobile", data.getMobile());
            payload.put("roles", data.getRoles());
            payload.put("formFields", data.getFormFields());
            payload.put("token", data.getToken());
            payload.put("expiresAt", data.getExpiresAt());
            payload.put("lockedFields", List.of("orgId", "email", "roles"));

            return ResponseEntity.ok(ResponseUtils.success(
                    "Prefilled data retrieved successfully", payload));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ResponseUtils.error("Error fetching prefilled data: " + e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // 🧾 Step 2: Complete Registration
    // -------------------------------------------------------------------------
    @PostMapping(value = "/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> completeRegistration(
            @RequestParam String token,
            @RequestBody Map<String, Object> userInput
    ) {
        try {
            System.out.println("[PrefilledController] Registration attempt for token: " + token);

            if (!tokenService.isTokenValid(token)) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtils.error("Invalid or expired token."));
            }

            // ❗ FIXED: userDataService returns entity, NOT Optional<PrefilledUserData>
            PrefilledUserData prefilled = userDataService.getByToken(token);
            if (prefilled == null) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtils.error("No prefilled data found."));
            }

            // Build DTO
            var dto = new com.medicconnect.dto.PersonDTO();
            dto.setOrgId(prefilled.getOrgId());
            dto.setEmail(prefilled.getEmail());
            dto.setName(prefilled.getName());
            dto.setMobile((String) userInput.getOrDefault("mobile", prefilled.getMobile()));
            dto.setPassword((String) userInput.getOrDefault("password", ""));
            dto.setRoles(prefilled.getRoles());

            // Merge admin fields + user inputs
            Map<String, Object> merged = new HashMap<>(prefilled.getFormFields());
            merged.putAll(userInput);
            dto.setAdditionalInfo(merged);

            Organization org = organizationService.findByOrgId(prefilled.getOrgId());
            Person savedUser = personService.createUser(dto);

            // Mark token used (except dev mode)
            if (!devMode) {
                tokenService.markTokenUsed(token);
                System.out.println("[PrefilledController] Token marked as used.");
            } else {
                System.out.println("[PrefilledController] Dev mode active — token NOT marked as used.");
            }

            Map<String, Object> result = Map.of(
                    "userId", savedUser.getUserId(),
                    "status", savedUser.getStatus(),
                    "orgId", org.getOrgId(),
                    "devMode", devMode
            );

            return ResponseEntity.ok(ResponseUtils.success("Registration completed successfully", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ResponseUtils.error("Registration failed: " + e.getMessage()));
        }
    }
}
