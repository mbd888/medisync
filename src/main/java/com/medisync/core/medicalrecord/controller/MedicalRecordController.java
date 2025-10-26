package com.medisync.core.medicalrecord.controller;

import com.medisync.core.medicalrecord.dto.CreateMedicalRecordRequest;
import com.medisync.core.medicalrecord.dto.MedicalRecordDTO;
import com.medisync.core.medicalrecord.dto.MedicalRecordListDTO;
import com.medisync.core.medicalrecord.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for medical record endpoints.
 * Endpoints:
 * - POST /api/medical-records - Create medical record (DOCTOR)
 * - GET /api/medical-records/{id} - View specific record
 * - GET /api/patients/medical-records - Patient's records (PATIENT)
 * - GET /api/doctors/medical-records - Doctor's records (DOCTOR)
 */
@RestController
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    // Create a new medical record after an appointment
    @PostMapping("/api/medical-records")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(
            Authentication authentication,
            @Valid @RequestBody CreateMedicalRecordRequest request
    ) {
        String doctorEmail = authentication.getName();
        MedicalRecordDTO record = medicalRecordService.createMedicalRecord(doctorEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    // Get a specific medical record by ID
    @GetMapping("/api/medical-records/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecord(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        MedicalRecordDTO record = medicalRecordService.getMedicalRecordById(id, userEmail);
        return ResponseEntity.ok(record);
    }

    // Get all medical records created by the current patient.
    @GetMapping("/api/patients/medical-records")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<MedicalRecordListDTO>> getPatientMedicalRecords(
            Authentication authentication
    ) {
        String patientEmail = authentication.getName();
        List<MedicalRecordListDTO> records = medicalRecordService.getPatientMedicalRecords(patientEmail);
        return ResponseEntity.ok(records);
    }

    // Get all medical records created by the current doctor.
    @GetMapping("/api/doctors/medical-records")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<MedicalRecordListDTO>> getDoctorMedicalRecords(
            Authentication authentication
    ) {
        String doctorEmail = authentication.getName();
        List<MedicalRecordListDTO> records = medicalRecordService.getDoctorMedicalRecords(doctorEmail);
        return ResponseEntity.ok(records);
    }
}
