package com.medisync.core.medicalrecord.entity;

import com.medisync.core.appointment.entity.Appointment;
import com.medisync.core.doctor.entity.Doctor;
import com.medisync.core.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MedicalRecord entity representing documentation of a patient visit.
 * Created by doctor after appointment is completed.
 * Contains diagnosis, symptoms, notes, and links to prescriptions/lab reports.
 * Relationships:
 * - ManyToOne with Patient (patient can have many records)
 * - ManyToOne with Doctor (doctor creates many records)
 * - OneToOne with Appointment (each record is for one appointment)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_records")
@EntityListeners(AuditingEntityListener.class)
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(length = 500)
    private String diagnosis;

    @Column(length = 1000)
    private String symptoms;

    @Column(length = 2000)
    private String notes;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
