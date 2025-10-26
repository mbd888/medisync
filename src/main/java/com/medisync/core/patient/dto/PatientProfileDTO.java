package com.medisync.core.patient.dto;

import com.medisync.core.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO for patient profile
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileDTO {

    private Long id;
    private String email;
    private Role role;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String address;

    private String bloodType;
    private String allergies;

    private String emergencyContactName;
    private String emergencyContactPhone;

    private String insuranceProvider;
    private String insurancePolicyNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
