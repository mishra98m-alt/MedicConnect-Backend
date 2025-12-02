package com.medicconnect.controllers;

import com.medicconnect.dto.PersonDTO;
import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import com.medicconnect.services.EmailService;
import com.medicconnect.services.FieldService;
import com.medicconnect.services.OrganizationService;
import com.medicconnect.services.PersonService;
import com.medicconnect.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final FieldService fieldService;
    private final OrganizationService organizationService;
    private final PersonService personService;
    private final EmailService emailService;

    public RegistrationController(FieldService fieldService,
                                  OrganizationService organizationService,
                                  PersonService personService,
                                  EmailService emailService) {
        this.fieldService = fieldService;
        this.organizationService = organizationService;
        this.personService = personService;
        this.emailService = emailService;
    }

    // ---------------- Fetch Registration Form ----------------
    @GetMapping("/register/form")
    public ResponseEntity<Map<String, Object>> getForm() {
        return ResponseEntity.ok(ResponseUtils.success(
                "Registration form fetched successfully",
                fieldService.getHospitalRegistrationForm()
        ));
    }

    // ---------------- Register Organization + Main Admin ----------------
    @PostMapping("/register/organization")
    public ResponseEntity<Map<String, Object>> registerOrganization(@RequestBody Map<String, Object> formData) {
        try {
            Map<String, Object> orgData = (Map<String, Object>) formData.get("organization");
            String orgName = (String) orgData.get("organizationName");

            Organization org = organizationService.findByOrganizationNameOptional(orgName)
                    .orElseGet(() -> organizationService.createOrganizationFromMap(orgData));

            Map<String, Object> personalData = (Map<String, Object>) formData.get("personal");
            Map<String, Object> authData = (Map<String, Object>) formData.get("auth");

            PersonDTO dto = buildPersonDTO(personalData, authData, org.getOrgId());

            Person savedAdmin = personService.createMainAdmin(dto);

            sendMainAdminRegistrationEmail(savedAdmin, org);

            return ResponseEntity.ok(ResponseUtils.success(
                    "Organization and Main Admin registered successfully",
                    Map.of("organizationId", org.getOrgId(), "adminUserId", savedAdmin.getUserId())
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseUtils.error(
                    "Failed to register organization or main admin", e.getMessage()
            ));
        }
    }

    // ---------------- Main Admin Creates Pre-Filled User Link ----------------
    @PostMapping("/register/user/pre-fill")
    public ResponseEntity<Map<String, Object>> createPreFilledUser(@RequestBody Map<String, Object> formData) {
        try {
            Map<String, Object> minimalData = (Map<String, Object>) formData.get("minimal");
            String orgId = (String) minimalData.get("orgId");

            // Generate temporary registration token
            String token = UUID.randomUUID().toString();

            // Save minimal info in a lightweight DTO for later completion (can also be saved in DB)
            PersonDTO dto = new PersonDTO();
            dto.setName((String) minimalData.get("name"));
            dto.setEmail((String) minimalData.get("email"));
            dto.setMobile((String) minimalData.get("mobile"));
            dto.setRoles((List<String>) minimalData.get("roles"));
            dto.setOrgId(orgId);
            dto.setTempToken(token);

            // Send email with pre-filled registration link
            String registrationLink = "http://your-frontend.com/register?token=" + token;
            emailService.sendEmail(dto.getEmail(),
                    "Complete Your Registration",
                    emailService.generatePreFilledRegistrationEmail(dto, registrationLink));

            return ResponseEntity.ok(ResponseUtils.success(
                    "Pre-filled registration link sent to user email",
                    Map.of("email", dto.getEmail(), "registrationLink", registrationLink)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseUtils.error(
                    "Failed to create pre-filled registration link", e.getMessage()
            ));
        }
    }

    // ---------------- Complete Registration From Pre-Filled Link ----------------
    @PostMapping("/register/user/complete")
    public ResponseEntity<Map<String, Object>> completeUserRegistration(@RequestBody Map<String, Object> formData) {
        try {
            String token = (String) formData.get("token");
            Map<String, Object> personalData = (Map<String, Object>) formData.get("personal");
            Map<String, Object> authData = (Map<String, Object>) formData.get("auth");
            Map<String, Object> orgData = (Map<String, Object>) formData.get("organization");
            String orgId = (String) orgData.get("orgId");

            PersonDTO dto = buildPersonDTO(personalData, authData, orgId);
            dto.setRoles((List<String>) formData.get("roles"));
            dto.setTempToken(token); // optional: validate token here

            Person savedUser = personService.createUser(dto);

            // Notify main admin
            notifyMainAdmin(savedUser);
            // Notify user
            notifyUserPending(savedUser);

            return ResponseEntity.ok(ResponseUtils.success(
                    "User registration completed successfully; pending main admin approval",
                    Map.of("organizationId", orgId, "userId", savedUser.getUserId(), "status", savedUser.getStatus())
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseUtils.error(
                    "Failed to complete user registration", e.getMessage()
            ));
        }
    }

    // ---------------- Helper Methods ----------------
    private PersonDTO buildPersonDTO(Map<String, Object> personalData, Map<String, Object> authData, String orgId) {
        PersonDTO dto = new PersonDTO();
        dto.setName((String) personalData.get("name"));
        dto.setEmail((String) personalData.get("email"));
        dto.setMobile((String) personalData.get("mobile"));
        dto.setDob(personalData.get("dob") != null ? LocalDate.parse(personalData.get("dob").toString()) : null);
        dto.setGender((String) personalData.get("gender"));
        dto.setBloodGroup((String) personalData.get("bloodGroup"));
        dto.setPassword((String) authData.get("password"));
        dto.setAgreement((Boolean) authData.get("agreement"));
        dto.setOrgId(orgId);
        return dto;
    }

    private void sendMainAdminRegistrationEmail(Person admin, Organization org) {
        if (admin.getEmail() != null) {
            try {
                emailService.sendEmail(admin.getEmail(),
                        "Medic-connect | Registration Successful",
                        emailService.generatePersonRegistrationSuccessEmail(
                                admin.getName(),
                                admin.getUserId(),
                                admin.getEmail(),
                                org.getOrganizationName(),
                                org.getOrgId(),
                                org.getCategory(),
                                LocalDateTime.now()
                        ));
            } catch (Exception e) {
                System.out.println("[EmailService] Failed to send registration email: " + e.getMessage());
            }
        }
    }

    private void notifyMainAdmin(Person user) {
        Optional<Person> mainAdminOpt = personService.getAllPersons().stream()
                .filter(p -> p.getOrganization().getOrgId().equals(user.getOrganization().getOrgId())
                        && p.getRoles().contains("MAIN_ADMIN"))
                .findFirst();
        mainAdminOpt.ifPresent(admin -> {
            try {
                emailService.sendEmail(admin.getEmail(),
                        "Medic-connect | New User Pending Approval",
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
                        ));
            } catch (Exception e) {
                System.out.println("[EmailService] Failed to notify main admin: " + e.getMessage());
            }
        });
    }

    private void notifyUserPending(Person user) {
        if (user.getEmail() != null) {
            try {
                emailService.sendEmail(user.getEmail(),
                        "Medic-connect | Registration Pending Approval",
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
            } catch (Exception e) {
                System.out.println("[EmailService] Failed to send pending email to user: " + e.getMessage());
            }
        }
    }
}
