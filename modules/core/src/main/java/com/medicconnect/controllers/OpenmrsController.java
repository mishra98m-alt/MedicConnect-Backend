package com.medicconnect.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.medicconnect.services.OpenmrsPatientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/openmrs")
@CrossOrigin("*")
public class OpenmrsController {

    private final OpenmrsPatientService patientService;

    public OpenmrsController(OpenmrsPatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Create a new patient in OpenMRS through MedicConnect backend
     */
    @PostMapping("/create-patient")
    public JsonNode createPatient(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String gender,
            @RequestParam String birthdate
    ) throws Exception {
        return patientService.createPatient(firstName, lastName, gender, birthdate);
    }
}
