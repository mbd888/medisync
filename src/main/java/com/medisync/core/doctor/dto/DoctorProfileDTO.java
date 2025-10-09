package com.medisync.core.doctor.dto;

import com.medisync.core.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for doctor profile responses.
 * This is what gets returned when a doctor views their profile:
 * - All user fields (email, role)
 * - All doctor-specific fields
 * - Timestamps
 * Never expose the password field!
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDTO {

    private Long id;
    private String email;
    private Role role;

    // Personal Information
    private String firstName;
    private String lastName;
    private String phone;

    // Professional Information
    private String specialization;
    private String licenseNumber;
    private String qualification;
    private Integer yearsOfExperience;
    private String bio;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
