package com.medicconnect.repository;

import com.medicconnect.models.PrefilledUserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrefilledUserDataRepository extends JpaRepository<PrefilledUserData, Long> {
    PrefilledUserData findByToken(String token);
    void deleteByToken(String token);
}
