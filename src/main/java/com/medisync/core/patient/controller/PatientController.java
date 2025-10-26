package com.medisync.core.patient.controller;

import com.medisync.core.patient.dto.PatientProfileDTO;
import com.medisync.core.patient.dto.UpdatePatientProfileRequest;
import com.medisync.core.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for patient profile endpoints.
 * Endpoints:
 * - GET /api/patients/profile - Get current patient's profile
 * - PUT /api/patients/profile - Update current patient's profile
 * Security:
 * - Only users with PATIENT role can access these endpoints
 * - Patients can only access their own profile (enforced by email from JWT)
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // Get current patient's profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();

        PatientProfileDTO profile = patientService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    // Update current patient's profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdatePatientProfileRequest request
    ) {

        String email = authentication.getName();

        PatientProfileDTO updatedProfile = patientService.updateProfile(email, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
