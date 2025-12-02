package com.medicconnect.services;

import com.medicconnect.models.PrefilledToken;
import com.medicconnect.repository.PrefilledTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrefilledTokenService {

    private final PrefilledTokenRepository repository;

    @Value("${app.dev-mode:false}")
    private boolean devMode;

    public PrefilledTokenService(PrefilledTokenRepository repository) {
        this.repository = repository;
    }

    /**
     * Save new token
     */
    public PrefilledToken saveToken(String token, String email, String orgId, long expiryMinutes) {
        PrefilledToken entity = new PrefilledToken();
        entity.setToken(token);
        entity.setEmail(email);
        entity.setOrgId(orgId);
        entity.setIssuedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        entity.setUsed(false);
        return repository.save(entity);
    }

    /**
     * Validate token
     */
    public boolean isTokenValid(String token) {

        if (devMode) {
            System.out.println("[PrefilledTokenService] Dev mode = ALWAYS VALID");
            return true;
        }

        PrefilledToken t = repository.findByToken(token);
        if (t == null) return false;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime graceExpiry = t.getExpiresAt().plusMinutes(10);

        return !t.isUsed() && graceExpiry.isAfter(now);
    }

    /**
     * Mark token as used
     */
    public void markTokenUsed(String token) {

        if (devMode) {
            System.out.println("[PrefilledTokenService] Dev mode — skipping markUsed()");
            return;
        }

        PrefilledToken t = repository.findByToken(token);
        if (t != null && !t.isUsed()) {
            t.setUsed(true);
            repository.save(t);
            System.out.println("[PrefilledTokenService] Token marked as used: " + token);
        }
    }

    /**
     * Get token details
     */
    public PrefilledToken getTokenDetails(String token) {
        return repository.findByToken(token);
    }

    /**
     * Cleanup expired tokens every 12 hours
     */
    @Scheduled(cron = "0 0 */12 * * *")
    public void cleanupExpiredTokens() {

        if (devMode) {
            System.out.println("[PrefilledTokenService] Dev mode — skipping cleanup");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<PrefilledToken> expired = repository.findByUsedFalseAndExpiresAtBefore(now);

        for (PrefilledToken t : expired) {
            repository.deleteByToken(t.getToken());
        }

        if (!expired.isEmpty()) {
            System.out.printf("[PrefilledTokenService] Cleaned %d expired tokens at %s%n",
                    expired.size(), now);
        }
    }
}
