package com.medisync.core.appointment.entity;

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
import java.time.LocalTime;

/**
 * Appointment entity representing a scheduled appointment between patient and doctor.
 * Relationships:
 * - ManyToOne with Patient (many appointments can belong to one patient)
 * - ManyToOne with Doctor (many appointments can be assigned to one doctor)
 * Business rules:
 * - appointmentDate must be in the future
 * - Default duration is 30 minutes
 * - Status starts as SCHEDULED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
@EntityListeners(AuditingEntityListener.class)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String notes; // Doctor's notes after appointment

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum for appointment status.
     */
    public enum AppointmentStatus {
        SCHEDULED,   // Appointment is booked
        COMPLETED,   // Patient came and appointment finished
        CANCELLED,   // Cancelled by patient or doctor
        NO_SHOW      // Patient didn't show up
    }
}
