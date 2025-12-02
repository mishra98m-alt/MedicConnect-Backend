package com.medicconnect.repository;

import com.medicconnect.models.PrefilledToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PrefilledTokenRepository extends JpaRepository<PrefilledToken, Long> {
    PrefilledToken findByToken(String token);
    void deleteByToken(String token);
    List<PrefilledToken> findByUsedFalseAndExpiresAtBefore(LocalDateTime dateTime);
}
