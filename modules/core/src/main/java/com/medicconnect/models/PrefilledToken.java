package com.medicconnect.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a pre-filled registration token.
 * Each token is linked to a user's email and organization,
 * and expires after a defined time window.
 */
@Entity
@Table(name = "prefilled_tokens")
public class PrefilledToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique JWT or generated token string for registration.
     */
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    /**
     * Email of the invited or prefilled user.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Associated organization ID for context validation.
     */
    @Column(nullable = false)
    private String orgId;

    /**
     * Token issuance timestamp.
     */
    @Column(nullable = false)
    private LocalDateTime issuedAt;

    /**
     * Expiry timestamp for the token.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Whether the token has been used to complete registration.
     */
    @Column(nullable = false)
    private boolean used = false;

    // --------------------------------------------------------
    // Getters & Setters
    // --------------------------------------------------------
    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    // --------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------
    @Transient
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "PrefilledToken{" +
                "token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", orgId='" + orgId + '\'' +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}
