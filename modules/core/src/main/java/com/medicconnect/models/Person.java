package com.medicconnect.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.medicconnect.permissions.Permission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------- User Info ----------------
    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private String userId;

    @Column(name = "personal_name", nullable = false)
    private String name;

    @Column(name = "personal_dob")
    private LocalDate dob;

    @Column(name = "personal_gender")
    private String gender;

    @Column(name = "personal_blood_group")
    private String bloodGroup;

    // ---------------- Contact Info ----------------
    @Column(name = "personal_email", unique = true, nullable = false)
    private String email;

    @Column(name = "personal_mobile", unique = true, nullable = false)
    private String mobile;

    // ---------------- Authentication ----------------
    @Column(name = "auth_password", nullable = false)
    private String password;

    @Column(name = "auth_agreement")
    private Boolean agreement;

    // ---------------- Roles & Status ----------------
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "person_roles", joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    // ---------------- Address ----------------
    private String fullAddress;
    private String country;
    private String state;
    private String city;
    private String pincode;

    // ---------------- Documents ----------------
    @Lob
    @Column(columnDefinition = "TEXT")
    private String documents;

    // ---------------- Metadata ----------------
    private LocalDateTime registrationDate;
    private LocalDateTime associatedDate;

    @ElementCollection(targetClass = Permission.class)
    @Enumerated(EnumType.STRING)
    private List<Permission> permissions;

    // ---------------- Organization ----------------
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id", nullable = false)
    @JsonBackReference
    private Organization organization;

    // ---------------- Dynamic Role-Specific Info ----------------
    @Lob
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfoJson; // stored as JSON

    @Transient
    private Map<String, Object> additionalInfo; // not persisted directly


    // ---------------- Lifecycle Hooks ----------------
    @PrePersist
    protected void onCreate() {
        if (this.userId == null) {
            this.userId = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.registrationDate == null) {
            this.registrationDate = LocalDateTime.now();
        }
        syncAdditionalInfoJson();
    }

    @PreUpdate
    protected void onUpdate() {
        syncAdditionalInfoJson();
    }

    @PostLoad
    protected void onLoad() {
        parseAdditionalInfoJson();
    }

    // ---------------- JSON Helper Methods ----------------
    private void syncAdditionalInfoJson() {
        try {
            if (additionalInfo != null && !additionalInfo.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                this.additionalInfoJson = mapper.writeValueAsString(additionalInfo);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing additional info", e);
        }
    }

    private void parseAdditionalInfoJson() {
        try {
            if (additionalInfoJson != null && !additionalInfoJson.isBlank()) {
                ObjectMapper mapper = new ObjectMapper();
                this.additionalInfo = mapper.readValue(additionalInfoJson, Map.class);
            } else {
                this.additionalInfo = new HashMap<>();
            }
        } catch (Exception e) {
            this.additionalInfo = new HashMap<>();
        }
    }

    // ---------------- Document Utilities ----------------
    public void appendDocument(String documentId) {
        if (this.documents == null || this.documents.isEmpty()) {
            this.documents = documentId;
        } else {
            this.documents += "," + documentId;
        }
    }

    public List<String> getDocumentIds() {
        if (documents == null || documents.isEmpty()) return List.of();
        return Arrays.stream(documents.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // ---------------- Getters & Setters ----------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getAgreement() { return agreement; }
    public void setAgreement(Boolean agreement) { this.agreement = agreement; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDateTime getAssociatedDate() { return associatedDate; }
    public void setAssociatedDate(LocalDateTime associatedDate) { this.associatedDate = associatedDate; }

    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public Map<String, Object> getAdditionalInfo() {
        if (additionalInfo == null) parseAdditionalInfoJson();
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
        syncAdditionalInfoJson();
    }

    public String getAdditionalInfoJson() { return additionalInfoJson; }
    public void setAdditionalInfoJson(String additionalInfoJson) {
        this.additionalInfoJson = additionalInfoJson;
        parseAdditionalInfoJson();
    }
}
