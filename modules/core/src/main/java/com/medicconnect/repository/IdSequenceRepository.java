package com.medicconnect.repository;

import com.medicconnect.models.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, Long> {

    // Must return entity, not Optional
    IdSequence findByTypeAndRole(String type, String role);
}
