package com.medisync.core.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a prescription to a medical record.
 * Doctor sends this to prescribe medication:
 * POST /api/medical-records/{id}/prescriptions
 * {
 *   "medicationName": "Amoxicillin",
 *   "dosage": "500mg",
 *   "frequency": "3 times daily",
 *   "duration": "7 days",
 *   "instructions": "Take with food"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPrescriptionRequest {

    @NotBlank(message = "Medication name is required")
    @Size(max = 200, message = "Medication name must not exceed 200 characters")
    private String medicationName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage must not exceed 100 characters")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Size(max = 100, message = "Frequency must not exceed 100 characters")
    private String frequency;

    @Size(max = 50, message = "Duration must not exceed 50 characters")
    private String duration;

    @Size(max = 500, message = "Instructions must not exceed 500 characters")
    private String instructions;
}
