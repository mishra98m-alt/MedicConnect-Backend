package com.medicconnect.controllers;

import com.medicconnect.dto.OpenmrsPatientPayload;
import com.medicconnect.models.Patient;
import com.medicconnect.services.OpenmrsPatientService;
import com.medicconnect.services.PatientService;
import com.medicconnect.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final OpenmrsPatientService openmrsPatientService;
    private final PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<?> createPatient(
            @RequestParam String orgId,
            @RequestBody OpenmrsPatientPayload payload
    ) {
        try {
            // 1️⃣ Create the patient in OpenMRS
            String uuid = openmrsPatientService.createPatient(payload);

            // 2️⃣ Store minimal patient record in MedicConnect DB
            Patient saved = patientService.savePatient(orgId, uuid);

            return ResponseEntity.ok(ResponseUtils.success(
                    "Patient created successfully",
                    Map.of(
                            "patientId", saved.getPatientId(),
                            "openmrsUuid", saved.getOpenmrsUuid(),
                            "orgId", saved.getOrgId()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtils.error("Failed to create patient: " + e.getMessage()));
        }
    }
}
