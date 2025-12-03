package com.medicconnect.controllers;

import com.medicconnect.dto.PersonDTO;
import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import com.medicconnect.models.PrefilledUserData;
import com.medicconnect.services.*;
import com.medicconnect.utils.JwtTokenUtil;
import com.medicconnect.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.medicconnect.utils.PrefilledLinkBuilder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;
    private final OrganizationService organizationService;
    private final EmailService emailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PrefilledTokenService prefilledTokenService;

    @Autowired
    private PrefilledUserDataService prefilledUserDataService;

    @Autowired
    private PrefilledLinkBuilder prefilledLinkBuilder;

    public PersonController(PersonService personService,
                            OrganizationService organizationService,
                            EmailService emailService) {
        this.personService = personService;
        this.organizationService = organizationService;
        this.emailService = emailService;
    }

    // --------------------------------------------------------------------
    // 🧑‍💼 Create Main Admin
    // --------------------------------------------------------------------
    @PostMapping("/main-admin")
    public ResponseEntity<?> createMainAdmin(@RequestBody PersonDTO dto) {
        try {
            Organization org = validateOrganization(dto.getOrgId());
            Person savedAdmin = personService.createMainAdmin(dto);

            sendEmail(savedAdmin, org,
                    "MedicConnect | Registration Successful",
                    emailService.generatePersonRegistrationSuccessEmail(
                            savedAdmin.getName(),
                            savedAdmin.getUserId(),
                            savedAdmin.getEmail(),
                            org.getOrganizationName(),
                            org.getOrgId(),
                            org.getCategory(),
                            savedAdmin.getAssociatedDate()
                    ));

            return ResponseEntity.ok(ResponseUtils.success(
                    "Main Admin created successfully",
                    Map.of("userId", savedAdmin.getUserId(), "orgId", org.getOrgId())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Failed to create main admin", e.getMessage()));
        }
    }

    // --------------------------------------------------------------------
    // 👥 Register Role-Based User
    // --------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> registerRoleUser(@RequestBody PersonDTO dto) {
        try {
            Organization org = validateOrganization(dto.getOrgId());
            Person savedUser = personService.createUser(dto);

            notifyMainAdmin(savedUser);
            notifyUserPending(savedUser);

            return ResponseEntity.ok(ResponseUtils.success(
                    "User registered successfully; pending main admin approval",
                    Map.of("userId", savedUser.getUserId(), "status", savedUser.getStatus())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Failed to register user", e.getMessage()));
        }
    }

    // --------------------------------------------------------------------
    // 📧 Create Prefilled Registration Link
    // --------------------------------------------------------------------
    @PostMapping("/register/user/pre-fill")
    public ResponseEntity<Map<String, Object>> createPreFilledUser(@RequestBody Map<String, Object> formData) {
        try {
            Map<String, Object> minimalData = (Map<String, Object>) formData.get("minimal");
            if (minimalData == null) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtils.error("Missing 'minimal' section in request body"));
            }

            String orgId = (String) minimalData.get("orgId");
            String email = (String) minimalData.get("email");

            if (orgId == null || orgId.isBlank() || email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtils.error("Both 'orgId' and 'email' are required"));
            }

            Organization org = validateOrganization(orgId);

            // Token creation
            Map<String, Object> claims = Map.of(
                    "orgId", orgId,
                    "email", email,
                    "organizationName", org.getOrganizationName(),
                    "tokenType", "PREFILLED_REGISTRATION"
            );
            String token = jwtTokenUtil.generateToken(claims);

            prefilledTokenService.saveToken(token, email, orgId, 14400);

            PrefilledUserData data = new PrefilledUserData();
            data.setOrgId(orgId);
            data.setEmail(email);
            data.setName((String) minimalData.getOrDefault("name", "User"));
            data.setMobile((String) minimalData.getOrDefault("mobile", ""));
            data.setRoles((List<String>) minimalData.getOrDefault("roles", List.of("USER")));
            data.setToken(token);

            Map<String, Object> dynamicFields = (Map<String, Object>) formData.get("fields");
            if (dynamicFields != null) {
                Map<String, String> converted = dynamicFields.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
                data.setFormFields(converted);
            }

            prefilledUserDataService.savePrefilledUserData(data);
            
            String prefilledLink = prefilledLinkBuilder.buildLink(token);

            // Send email
            PersonDTO dto = new PersonDTO();
            dto.setName(data.getName());
            dto.setEmail(email);
            dto.setMobile(data.getMobile());
            dto.setRoles(data.getRoles());
            dto.setOrgId(orgId);

            String htmlEmail = emailService.generatePreFilledRegistrationEmail(dto, prefilledLink);
            emailService.sendEmail(email, "MedicConnect | Complete Your Registration", htmlEmail);

            return ResponseEntity.ok(ResponseUtils.success(
                    "Pre-filled registration link created successfully",
                    Map.of("token", token, "prefilledLink", prefilledLink)
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Error creating pre-filled link: " + e.getMessage()));
        }
    }

    // --------------------------------------------------------------------
    // 📋 Fetch Prefilled Data
    // --------------------------------------------------------------------
    @GetMapping("/register/user/fetch")
    public ResponseEntity<Map<String, Object>> fetchPreFilledUser(@RequestParam("token") String token) {
        try {
            if (!prefilledTokenService.isTokenValid(token)) {
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid or expired token"));
            }

            PrefilledUserData data = prefilledUserDataService.getByToken(token);
            if (data == null) {
                return ResponseEntity.badRequest().body(ResponseUtils.error("No prefilled data found"));
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("orgId", data.getOrgId());
            response.put("email", data.getEmail());
            response.put("name", data.getName());
            response.put("mobile", data.getMobile());
            response.put("roles", data.getRoles());
            response.put("formFields", data.getFormFields());
            response.put("expiresAt", data.getExpiresAt());
            response.put("lockedFields", List.of("orgId", "email", "roles"));

            return ResponseEntity.ok(ResponseUtils.success("Prefilled data fetched successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Failed to fetch prefilled data", e.getMessage()));
        }
    }

    // --------------------------------------------------------------------
    // 🧾 Complete Prefilled Registration
    // --------------------------------------------------------------------
    @PostMapping("/register/user/complete")
    public ResponseEntity<Map<String, Object>> completePreFilledRegistration(
           @RequestParam("token") String token,
           @RequestBody Map<String, Object> userInput
    ) {
        try {
            if (!prefilledTokenService.isTokenValid(token)) {
                return ResponseEntity.badRequest().body(ResponseUtils.error("Invalid or expired token"));
            }

            PrefilledUserData prefilled = prefilledUserDataService.getByToken(token);
            if (prefilled == null) {
                return ResponseEntity.badRequest().body(ResponseUtils.error("No prefilled data found"));
            }

            PersonDTO dto = new PersonDTO();
            dto.setOrgId(prefilled.getOrgId());
            dto.setEmail(prefilled.getEmail());
            dto.setName(prefilled.getName());
            dto.setMobile((String) userInput.getOrDefault("mobile", prefilled.getMobile()));
            dto.setRoles(prefilled.getRoles());
            dto.setPassword((String) userInput.get("password"));

            Map<String, Object> merged = new HashMap<>(prefilled.getFormFields());
            merged.putAll(userInput);
            dto.setAdditionalInfo(merged);

            Organization org = validateOrganization(prefilled.getOrgId());
            Person savedUser = personService.createUser(dto);

            prefilledTokenService.markTokenUsed(token);
            notifyMainAdmin(savedUser);
            notifyUserPending(savedUser);

            return ResponseEntity.ok(ResponseUtils.success(
                    "Registration completed successfully. Pending main admin approval.",
                    Map.of("userId", savedUser.getUserId(), "status", savedUser.getStatus(), "orgId", org.getOrgId())
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.error("Failed to complete prefilled registration", e.getMessage()));
        }
    }

    // --------------------------------------------------------------------
    // 🔧 Helper Methods
    // --------------------------------------------------------------------
    private Organization validateOrganization(String orgId) {
        if (orgId == null || orgId.isBlank()) throw new RuntimeException("Organization ID is required");
        Organization org = organizationService.findByOrgId(orgId);
        if (org == null) throw new RuntimeException("Organization not found");
        return org;
    }

    private void sendEmail(Person person, Organization org, String subject, String body) {
        if (person.getEmail() != null) {
            try {
                emailService.sendEmail(person.getEmail(), subject, body);
            } catch (Exception e) {
                System.err.println("[EmailService] Failed to send email: " + e.getMessage());
            }
        }
    }

    private void notifyMainAdmin(Person user) {
        personService.getAllPersons().stream()
                .filter(p -> p.getOrganization() != null
                        && user.getOrganization() != null
                        && p.getOrganization().getOrgId().equals(user.getOrganization().getOrgId())
                        && p.getRoles() != null
                        && p.getRoles().contains("MAIN_ADMIN"))
                .findFirst()
                .ifPresent(admin -> sendEmail(admin, user.getOrganization(),
                        "MedicConnect | New User Pending Approval",
                        emailService.generateNewUserNotificationForAdmin(
                                admin.getName(),
                                user.getName(),
                                user.getEmail(),
                                user.getUserId(),
                                user.getRoles(),
                                user.getOrganization().getOrganizationName(),
                                user.getOrganization().getOrgId(),
                                user.getOrganization().getCategory(),
                                user.getAssociatedDate()
                        )));
    }

    private void notifyUserPending(Person user) {
        sendEmail(user, user.getOrganization(),
                "MedicConnect | Registration Pending Approval",
                emailService.generateUserRegistrationPendingEmail(
                        user.getName(),
                        user.getUserId(),
                        user.getEmail(),
                        user.getRoles(),
                        user.getOrganization().getOrganizationName(),
                        user.getOrganization().getOrgId(),
                        user.getOrganization().getCategory(),
                        user.getAssociatedDate()
                ));
    }
}
