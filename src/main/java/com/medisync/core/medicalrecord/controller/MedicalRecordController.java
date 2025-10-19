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

    /**
     * Create a medical record after an appointment.
     * POST /api/medical-records
     * Authorization: Bearer <doctor_token>
     * Content-Type: application/json
     * Request body:
     * {
     *   "appointmentId": 1,
     *   "diagnosis": "Common Cold",
     *   "symptoms": "Fever, cough",
     *   "notes": "Rest recommended",
     *   "followUpDate": "2025-11-01"
     * }
     *
     * @param authentication Spring Security authentication
     * @param request medical record details
     * @return created medical record
     */
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

    /**
     * Get a specific medical record by ID.
     * GET /api/medical-records/{id}
     * Authorization: Bearer <token>
     *
     * @param id medical record ID
     * @param authentication Spring Security authentication
     * @return medical record with prescriptions and lab reports
     */
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

    /**
     * Get all medical records for the current patient.
     * GET /api/patients/medical-records
     * Authorization: Bearer <patient_token>
     * Response: List of medical records (simplified view)
     *
     * @param authentication Spring Security authentication
     * @return list of patient's medical records
     */
    @GetMapping("/api/patients/medical-records")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<MedicalRecordListDTO>> getPatientMedicalRecords(
            Authentication authentication
    ) {
        String patientEmail = authentication.getName();
        List<MedicalRecordListDTO> records = medicalRecordService.getPatientMedicalRecords(patientEmail);
        return ResponseEntity.ok(records);
    }

    /**
     * Get all medical records created by the current doctor.
     * GET /api/doctors/medical-records
     * Authorization: Bearer <doctor_token>
     * Response: List of medical records (simplified view)
     *
     * @param authentication Spring Security authentication
     * @return list of doctor's medical records
     */
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
