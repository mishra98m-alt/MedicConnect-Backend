package com.medicconnect.controllers;

import com.medicconnect.models.FormBlock;
import com.medicconnect.models.FormField;
import com.medicconnect.services.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FieldService fieldService;

    public FormController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    /**
     * Base registration form (common fields only)
     * Example: GET /api/forms/base
     */
    @GetMapping("/base")
    public ResponseEntity<List<FormBlock>> getBaseForm() {
        return ResponseEntity.ok(fieldService.getBaseFormAsBlocks());
    }

    /**
     * Role-specific dynamic form (Doctor, Admin, etc.)
     * Only returns fields specific to the role, without the full base form
     * Example: GET /api/forms/role-fields?role=Doctor
     */
    @GetMapping("/role-fields")
    public ResponseEntity<List<FormField>> getRoleSpecificFields(@RequestParam String role) {
        List<FormField> roleFields = fieldService.getFieldsForRole(role.trim());
        return ResponseEntity.ok(roleFields);
    }

    /**
     * Hospital registration form (organization + admin)
     */
    @GetMapping("/hospital-registration")
    public ResponseEntity<List<FormBlock>> getHospitalRegistrationForm() {
        return ResponseEntity.ok(fieldService.getHospitalRegistrationForm());
    }

    /**
     * Main admin registration form
     */
    @GetMapping("/main-admin-registration")
    public ResponseEntity<List<FormBlock>> getMainAdminRegistrationForm() {
        return ResponseEntity.ok(fieldService.getMainAdminRegistrationForm());
    }

    /**
     * User registration form (base form)
     */
    @GetMapping("/user-registration")
    public ResponseEntity<List<FormBlock>> getUserRegistrationForm() {
        return ResponseEntity.ok(fieldService.getUserRegistrationForm());
    }
}
