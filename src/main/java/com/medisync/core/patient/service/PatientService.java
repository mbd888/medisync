package com.medisync.core.patient.service;

import com.medisync.core.exception.ResourceNotFoundException;
import com.medisync.core.patient.dto.PatientProfileDTO;
import com.medisync.core.patient.dto.UpdatePatientProfileRequest;
import com.medisync.core.patient.entity.Patient;
import com.medisync.core.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for patient profile operations.
 * Handles:
 * - Getting patient profile by email
 * - Updating patient profile
 * - Converting between Entity and DTO
 */
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Get patient profile by email.
     *
     * @param email patient's email
     * @return patient profile DTO
     * @throws ResourceNotFoundException if patient not found
     */
    @Transactional(readOnly = true)
    public PatientProfileDTO getProfile(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with email: " + email
                ));

        return mapToDTO(patient);
    }

    /**
     * Update patient profile.
     * Only updates fields that are not null in the request.
     * This allows partial updates.
     *
     * @param email patient's email
     * @param request update request with new values
     * @return updated patient profile DTO
     * @throws ResourceNotFoundException if patient not found
     */
    @Transactional
    public PatientProfileDTO updateProfile(String email, UpdatePatientProfileRequest request) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with email: " + email
                ));

        // Update only non-null fields (partial update)
        if (request.getFirstName() != null) {
            patient.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            patient.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            patient.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        if (request.getPhone() != null) {
            patient.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getBloodType() != null) {
            patient.setBloodType(request.getBloodType());
        }
        if (request.getAllergies() != null) {
            patient.setAllergies(request.getAllergies());
        }
        if (request.getEmergencyContactName() != null) {
            patient.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getInsuranceProvider() != null) {
            patient.setInsuranceProvider(request.getInsuranceProvider());
        }
        if (request.getInsurancePolicyNumber() != null) {
            patient.setInsurancePolicyNumber(request.getInsurancePolicyNumber());
        }

        // Save and return updated profile
        Patient updatedPatient = patientRepository.save(patient);
        return mapToDTO(updatedPatient);
    }

    /**
     * Convert Patient entity to PatientProfileDTO.
     *
     * @param patient patient entity
     * @return patient profile DTO
     */
    private PatientProfileDTO mapToDTO(Patient patient) {
        return PatientProfileDTO.builder()
                .id(patient.getId())
                .email(patient.getEmail())
                .role(patient.getRole())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .bloodType(patient.getBloodType())
                .allergies(patient.getAllergies())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .insuranceProvider(patient.getInsuranceProvider())
                .insurancePolicyNumber(patient.getInsurancePolicyNumber())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
