package com.medisync.core.patient.service;

import com.medisync.core.exception.ResourceNotFoundException;
import com.medisync.core.patient.dto.PatientProfileDTO;
import com.medisync.core.patient.dto.UpdatePatientProfileRequest;
import com.medisync.core.patient.entity.Patient;
import com.medisync.core.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service for patient's profile operations
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    // Get current patient's profile
    @Transactional(readOnly = true)
    public PatientProfileDTO getProfile(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with email: " + email
                ));

        return mapToDTO(patient);
    }

    // Update current patient's profile
    @Transactional
    public PatientProfileDTO updateProfile(String email, UpdatePatientProfileRequest request) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with email: " + email
                ));

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

        Patient updatedPatient = patientRepository.save(patient);
        return mapToDTO(updatedPatient);
    }

    // Convert Patient entity to full PatientDTO
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
