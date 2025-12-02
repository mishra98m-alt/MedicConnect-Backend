package com.medicconnect.controllers;

import com.medicconnect.dto.OrganizationDTO;
import com.medicconnect.models.Organization;
import com.medicconnect.services.EmailService;
import com.medicconnect.services.OrganizationService;
import com.medicconnect.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final EmailService emailService;

    public OrganizationController(OrganizationService organizationService, EmailService emailService) {
        this.organizationService = organizationService;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrganizations() {
        List<Organization> orgs = organizationService.getAllOrganizations();
        return ResponseEntity.ok(ResponseUtils.success("Organizations fetched successfully", orgs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganizationById(@PathVariable String id) {
        try {
            Organization org = organizationService.getOrganizationById(id);
            return ResponseEntity.ok(ResponseUtils.success("Organization fetched successfully", org));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationDTO dto) {
        try {
            Organization org = organizationService.createOrganization(dto.toOrganization());

            if (org.getEmail() != null) {
                String htmlBody = emailService.generateOrgRegistrationSuccessEmail(
                        org.getOrganizationName(), org.getCategory(), org.getRegistrationNumber(), org.getOrgId()
                );
                emailService.sendEmail(org.getEmail(),
                        "Organization Registration Successful - " + org.getOrganizationName(),
                        htmlBody
                );
            }

            return ResponseEntity.ok(ResponseUtils.success("Organization registered successfully", org));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrganization(@PathVariable String id, @RequestBody OrganizationDTO dto) {
        try {
            Organization updated = organizationService.updateOrganization(id, dto);
            return ResponseEntity.ok(ResponseUtils.success("Organization updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable String id) {
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.ok(ResponseUtils.success("Organization deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseUtils.error(e.getMessage(), null));
        }
    }
}
