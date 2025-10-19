package com.medisync.core.medicalrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Simplified DTO for listing medical records.
 * Used when showing a patient's medical history timeline.
 * Less detail than full MedicalRecordDTO for better performance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordListDTO {

    private Long id;
    private LocalDate visitDate;
    private String diagnosis;
    private String doctorName;
    private String doctorSpecialization;
    private LocalDate followUpDate;
    private Integer prescriptionCount;
    private Integer labReportCount;
}
