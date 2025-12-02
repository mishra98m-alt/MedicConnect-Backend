package com.medicconnect.models;

import jakarta.persistence.*;

/**
 * Entity representing the last generated ID for a given type and role.
 * Used to maintain sequential IDs for organizations and users.
 */
@Entity
@Table(
    name = "id_sequences",
    uniqueConstraints = @UniqueConstraint(columnNames = {"type", "role"})
)
public class IdSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of entity: "ORG" for organizations, "USER" for users.
     */
    @Column(nullable = false)
    private String type;

    /**
     * Role of the user (e.g., ADMIN, DOCTOR, PATIENT). Null for organizations.
     */
    @Column
    private String role;

    /**
     * Last generated sequential value for this type and role.
     */
    @Column(name = "`last_value`", nullable = false)
    private Long lastValue = 0L;

    // Constructors
    public IdSequence() {}

    public IdSequence(String type, String role, Long lastValue) {
        this.type = type;
        this.role = role;
        this.lastValue = lastValue;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getLastValue() { return lastValue; }
    public void setLastValue(Long lastValue) { this.lastValue = lastValue; }
}
