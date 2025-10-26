package com.medisync.core.doctor.controller;

import com.medisync.core.doctor.dto.DoctorProfileDTO;
import com.medisync.core.doctor.dto.UpdateDoctorProfileRequest;
import com.medisync.core.doctor.repository.DoctorRepository;
import com.medisync.core.doctor.service.DoctorService;
import com.medisync.core.doctor.entity.Doctor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.List;


/**
 * REST Controller for doctor profile endpoints.
 * Endpoints:
 * - GET /api/doctors/profile - Get current doctor's profile
 * - PUT /api/doctors/profile - Update current doctor's profile
 * Security:
 * - Only users with DOCTOR role can access these endpoints
 * - Doctors can only access their own profile (enforced by email from JWT)
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;

    // Get current doctor's profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileDTO> getProfile(Authentication authentication) {
        // Get email from JWT token (automatically provided by Spring Security)
        String email = authentication.getName();

        DoctorProfileDTO profile = doctorService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    // Update current doctor's profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateDoctorProfileRequest request
    ) {

        String email = authentication.getName();

        DoctorProfileDTO updatedProfile = doctorService.updateProfile(email, request);
        return ResponseEntity.ok(updatedProfile);
    }

    // Get all doctors
    @GetMapping
    public ResponseEntity<List<DoctorProfileDTO>> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        List<DoctorProfileDTO> doctorDTOs = doctors.stream()
                .map(doctor -> DoctorProfileDTO.builder()
                        .id(doctor.getId())
                        .email(doctor.getEmail())
                        .role(doctor.getRole())
                        .firstName(doctor.getFirstName())
                        .lastName(doctor.getLastName())
                        .phone(doctor.getPhone())
                        .specialization(doctor.getSpecialization())
                        .licenseNumber(doctor.getLicenseNumber())
                        .qualification(doctor.getQualification())
                        .yearsOfExperience(doctor.getYearsOfExperience())
                        .bio(doctor.getBio())
                        .createdAt(doctor.getCreatedAt())
                        .updatedAt(doctor.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(doctorDTOs);
    }
}
