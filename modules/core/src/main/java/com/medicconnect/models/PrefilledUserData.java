package com.medicconnect.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "prefilled_user_data")
public class PrefilledUserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -----------------------------
    // Basic User Information
    // -----------------------------
    private String orgId;
    private String email;
    private String name;
    private String mobile;

    // -----------------------------
    // Roles (supports multiple)
    // -----------------------------
    @ElementCollection
    @CollectionTable(name = "prefilled_user_roles", joinColumns = @JoinColumn(name = "user_data_id"))
    @Column(name = "role")
    private List<String> roles;

    // -----------------------------
    // Dynamic Field Storage
    // -----------------------------
    @ElementCollection
    @CollectionTable(name = "prefilled_user_fields", joinColumns = @JoinColumn(name = "user_data_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value", columnDefinition = "TEXT")
    private Map<String, String> formFields = new HashMap<>();

    // -----------------------------
    // Token Management
    // -----------------------------
    @Column(length = 512, unique = true, nullable = false)
    private String token;

    private boolean used = false;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // -----------------------------
    // Lifecycle Hooks
    // -----------------------------
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.expiresAt == null) {
            // 🔥 Token validity set to 10 days by default
            this.expiresAt = LocalDateTime.now().plusDays(10);
        }
    }

    // -----------------------------
    // Getters & Setters
    // -----------------------------
    public Long getId() {
        return id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<String, String> getFormFields() {
        return formFields;
    }

    public void setFormFields(Map<String, String> formFields) {
        this.formFields = formFields;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
