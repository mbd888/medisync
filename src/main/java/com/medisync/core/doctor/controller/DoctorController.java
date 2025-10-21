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

    /**
     * Get current doctor's profile.
     * GET /api/doctors/profile
     * Authorization: Bearer <token>
     * Response (200 OK):
     * {
     *   "id": 1,
     *   "email": "doctor@test.com",
     *   "role": "DOCTOR",
     *   "firstName": "Jane",
     *   "lastName": "Smith",
     *   "specialization": "Cardiology",
     *   ...
     * }
     *
     * @param authentication Spring Security authentication object (contains user email)
     * @return doctor profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileDTO> getProfile(Authentication authentication) {
        // Get email from JWT token (automatically provided by Spring Security)
        String email = authentication.getName();

        DoctorProfileDTO profile = doctorService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update current doctor's profile.
     * PUT /api/doctors/profile
     * Authorization: Bearer <token>
     * Content-Type: application/json
     * Request body (all fields optional):
     * {
     *   "firstName": "Jane",
     *   "lastName": "Smith",
     *   "phone": "1234567890",
     *   "specialization": "Cardiology",
     *   "licenseNumber": "MD123456",
     *   ...
     * }
     * Response (200 OK):
     * {
     *   "id": 1,
     *   "email": "doctor@test.com",
     *   "firstName": "Jane",
     *   "lastName": "Smith",
     *   ...
     * }
     *
     * @param authentication Spring Security authentication object
     * @param request update request with new values
     * @return updated doctor profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateDoctorProfileRequest request
    ) {
        // Get email from JWT token
        String email = authentication.getName();

        DoctorProfileDTO updatedProfile = doctorService.updateProfile(email, request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Get all doctors (public endpoint for patients to browse).
     * GET /api/doctors
     * Response: List of doctor profiles
     */
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
