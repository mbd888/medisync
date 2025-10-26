package com.medisync.core.medicalrecord.controller;

import com.medisync.core.medicalrecord.dto.AddPrescriptionRequest;
import com.medisync.core.medicalrecord.dto.PrescriptionDTO;
import com.medisync.core.medicalrecord.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for prescription endpoints.
 * Endpoints:
 * - POST /api/medical-records/{id}/prescriptions - Add prescription (DOCTOR)
 * - GET /api/medical-records/{id}/prescriptions - View prescriptions
 */
@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // Add a prescription to a medical record.
    @PostMapping("/{id}/prescriptions")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionDTO> addPrescription(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody AddPrescriptionRequest request
    ) {
        String doctorEmail = authentication.getName();
        PrescriptionDTO prescription = prescriptionService.addPrescription(id, request, doctorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
    }

    // Get all prescriptions for a medical record.
    @GetMapping("/{id}/prescriptions")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(@PathVariable Long id) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptions(id);
        return ResponseEntity.ok(prescriptions);
    }
}
