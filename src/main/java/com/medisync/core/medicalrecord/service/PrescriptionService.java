package com.medisync.core.medicalrecord.service;

import com.medisync.core.exception.MedicalRecordNotFoundException;
import com.medisync.core.medicalrecord.dto.AddPrescriptionRequest;
import com.medisync.core.medicalrecord.dto.PrescriptionDTO;
import com.medisync.core.medicalrecord.entity.MedicalRecord;
import com.medisync.core.medicalrecord.entity.Prescription;
import com.medisync.core.medicalrecord.repository.MedicalRecordRepository;
import com.medisync.core.medicalrecord.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for prescription operations.
 * Handles:
 * - Adding prescriptions to medical records
 * - Viewing prescriptions
 */
@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * Add a prescription to a medical record.
     *
     * @param medicalRecordId medical record ID
     * @param request prescription details
     * @param doctorEmail doctor's email (for security check)
     * @return created prescription DTO
     * @throws MedicalRecordNotFoundException if record not found
     */
    @Transactional
    public PrescriptionDTO addPrescription(Long medicalRecordId, AddPrescriptionRequest request, String doctorEmail) {
        // Find medical record
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + medicalRecordId
                ));

        // Security check: only the doctor who created the record can add prescriptions
        if (!medicalRecord.getDoctor().getEmail().equals(doctorEmail)) {
            throw new SecurityException("You can only add prescriptions to your own medical records");
        }

        // Create prescription
        Prescription prescription = Prescription.builder()
                .medicalRecord(medicalRecord)
                .medicationName(request.getMedicationName())
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .duration(request.getDuration())
                .instructions(request.getInstructions())
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return mapToDTO(savedPrescription);
    }

    /**
     * Get all prescriptions for a medical record.
     *
     * @param medicalRecordId medical record ID
     * @return list of prescriptions
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getPrescriptions(Long medicalRecordId) {
        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecord_Id(medicalRecordId);
        return prescriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Prescription entity to DTO.
     */
    private PrescriptionDTO mapToDTO(Prescription prescription) {
        return PrescriptionDTO.builder()
                .id(prescription.getId())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .duration(prescription.getDuration())
                .instructions(prescription.getInstructions())
                .createdAt(prescription.getCreatedAt())
                .build();
    }
}
