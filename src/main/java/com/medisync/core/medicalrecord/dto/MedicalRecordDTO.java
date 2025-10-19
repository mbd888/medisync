package com.medisync.core.medicalrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Full DTO for medical record details.
 * Includes all information about the visit, prescriptions, and lab reports.
 * Used when viewing a specific medical record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {

    private Long id;
    private LocalDate visitDate;
    private String diagnosis;
    private String symptoms;
    private String notes;
    private LocalDate followUpDate;

    // Patient info
    private PatientInfo patient;

    // Doctor info
    private DoctorInfo doctor;

    // Related prescriptions
    private List<PrescriptionDTO> prescriptions;

    // Related lab reports
    private List<LabReportDTO> labReports;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested DTO for patient information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    /**
     * Nested DTO for doctor information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String specialization;
    }
}
