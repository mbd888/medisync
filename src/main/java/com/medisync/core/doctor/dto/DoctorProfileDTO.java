package com.medisync.core.doctor.dto;

import com.medisync.core.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO for doctor profile.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDTO {

    private Long id;
    private String email;
    private Role role;

    private String firstName;
    private String lastName;
    private String phone;

    private String specialization;
    private String licenseNumber;
    private String qualification;
    private Integer yearsOfExperience;
    private String bio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
