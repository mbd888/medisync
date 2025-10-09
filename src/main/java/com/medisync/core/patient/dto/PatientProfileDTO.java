package com.medisync.core.patient.dto;

import com.medisync.core.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for patient profile responses.
 * This is what gets returned when a patient views their profile:
 * - All user fields (email, role)
 * - All patient-specific fields
 * - Timestamps
 * Never expose the password field!
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileDTO {

    private Long id;
    private String email;
    private Role role;

    // Personal Information
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String address;

    // Medical Information
    private String bloodType;
    private String allergies;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Insurance Information
    private String insuranceProvider;
    private String insurancePolicyNumber;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
