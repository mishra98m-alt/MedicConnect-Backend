package com.medicconnect.services;

import com.medicconnect.models.IdSequence;
import com.medicconnect.repository.IdSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdGeneratorService {

    private final IdSequenceRepository sequenceRepository;

    public IdGeneratorService(IdSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    // ============================
    // Generate Organization ID
    // ============================
    @Transactional
    public synchronized String generateOrgId() {

        String type = "ORG";

        // FIX: repository does NOT return Optional
        IdSequence seq = sequenceRepository.findByTypeAndRole(type, null);

        if (seq == null) {
            seq = new IdSequence();
            seq.setType(type);
            seq.setRole(null);
            seq.setLastValue(0L);
        }

        long nextValue = seq.getLastValue() + 1;
        seq.setLastValue(nextValue);
        sequenceRepository.save(seq);

        return "ORG" + (1000 + nextValue);
    }

    // ============================
    // Generate User ID
    // ============================
    @Transactional
    public synchronized String generateUserId(String role) {

        String type = "USER";
        String roleKey = (role != null) ? role.toUpperCase() : "USER";

        // FIX: repository does NOT return Optional
        IdSequence seq = sequenceRepository.findByTypeAndRole(type, roleKey);

        if (seq == null) {
            seq = new IdSequence();
            seq.setType(type);
            seq.setRole(roleKey);
            seq.setLastValue(0L);
        }

        long nextValue = seq.getLastValue() + 1;
        seq.setLastValue(nextValue);
        sequenceRepository.save(seq);

        String prefix = switch (roleKey) {
            case "ADMIN" -> "ADM";
            case "DOCTOR" -> "DOC";
            case "PATIENT" -> "PAT";
            default -> "USR";
        };

        return prefix + (1000 + nextValue);
    }
}
