package com.medisync.core.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DTO for creating a new medical record.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @Size(max = 500, message = "Diagnosis must not exceed 500 characters")
    private String diagnosis;

    @Size(max = 1000, message = "Symptoms must not exceed 1000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    private LocalDate followUpDate;
}
