package com.medisync.core.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Prescription entity representing medication prescribed to a patient.
 * Belongs to a MedicalRecord (created during a visit).
 * Contains medication details, dosage, frequency, and instructions.
 * Example:
 * - Medication: Amoxicillin
 * - Dosage: 500mg
 * - Frequency: 3 times daily
 * - Duration: 7 days
 * - Instructions: Take with food
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prescriptions")
@EntityListeners(AuditingEntityListener.class)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "medication_name", nullable = false, length = 200)
    private String medicationName;

    @Column(nullable = false, length = 100)
    private String dosage; // e.g., "500mg", "10ml"

    @Column(nullable = false, length = 100)
    private String frequency; // e.g., "twice daily", "every 6 hours"

    @Column(length = 50)
    private String duration; // e.g., "7 days", "2 weeks"

    @Column(length = 500)
    private String instructions; // e.g., "Take with food", "Avoid alcohol"

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
