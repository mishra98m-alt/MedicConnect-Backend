package com.medicconnect.services;

import com.medicconnect.models.PrefilledUserData;
import com.medicconnect.repository.PrefilledUserDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrefilledUserDataService {

    private final PrefilledUserDataRepository repository;

    public PrefilledUserDataService(PrefilledUserDataRepository repository) {
        this.repository = repository;
    }

    // ---------------- Save User Data ----------------
    public PrefilledUserData savePrefilledUserData(PrefilledUserData data) {
        if (data.getExpiresAt() == null) {
            data.setExpiresAt(LocalDateTime.now().plusDays(10));
        }
        return repository.save(data);
    }

    // ---------------- Get by Token ----------------
    public PrefilledUserData getByToken(String token) {
        return repository.findByToken(token);
    }

    // ---------------- Validate Token ----------------
    public boolean isTokenValid(String token) {

        PrefilledUserData data = repository.findByToken(token);
        if (data == null) return false;

        return !data.isUsed() && data.getExpiresAt().isAfter(LocalDateTime.now());
    }

    // ---------------- Mark Token Used ----------------
    public void markUsed(String token) {

        PrefilledUserData data = repository.findByToken(token);
        if (data != null) {
            data.setUsed(true);
            repository.save(data);
        }
    }

    // ---------------- Cleanup Expired Tokens ----------------
    public void cleanupExpired() {

        List<PrefilledUserData> all = repository.findAll();

        for (PrefilledUserData d : all) {
            if (d.isUsed() || d.getExpiresAt().isBefore(LocalDateTime.now())) {
                repository.deleteByToken(d.getToken());
            }
        }
    }
}
