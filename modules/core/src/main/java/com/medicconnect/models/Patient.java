package com.medicconnect.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "patients")
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // internal PK

    @Column(name = "patient_id", unique = true, nullable = false)
    private String patientId;   // MedicConnect patient code

    @Column(name = "org_id", nullable = false)
    private String orgId;

    @Column(name = "openmrs_uuid", nullable = false)
    private String openmrsUuid;
}
