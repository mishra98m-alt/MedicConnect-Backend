package com.medicconnect.services;

import com.medicconnect.models.Patient;
import com.medicconnect.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final IdGeneratorService idGeneratorService;

    public Patient savePatient(String orgId, String openmrsUuid) {

        String patientId = idGeneratorService.generateUserId("PATIENT");

        Patient p = new Patient();
        p.setOrgId(orgId);
        p.setOpenmrsUuid(openmrsUuid);
        p.setPatientId(patientId);

        return patientRepository.save(p);
    }
}
