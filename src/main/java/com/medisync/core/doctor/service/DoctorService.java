package com.medisync.core.doctor.service;

import com.medisync.core.doctor.dto.DoctorProfileDTO;
import com.medisync.core.doctor.dto.UpdateDoctorProfileRequest;
import com.medisync.core.doctor.entity.Doctor;
import com.medisync.core.doctor.repository.DoctorRepository;
import com.medisync.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for doctor profile operations.
 * Handles:
 * - Getting doctor profile by email
 * - Updating doctor profile
 * - Converting between Entity and DTO
 */
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    /**
     * Get doctor profile by email.
     *
     * @param email doctor's email
     * @return doctor profile DTO
     * @throws ResourceNotFoundException if doctor not found
     */
    @Transactional(readOnly = true)
    public DoctorProfileDTO getProfile(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with email: " + email
                ));

        return mapToDTO(doctor);
    }

    /**
     * Update doctor profile.
     * Only updates fields that are not null in the request.
     * This allows partial updates.
     *
     * @param email doctor's email
     * @param request update request with new values
     * @return updated doctor profile DTO
     * @throws ResourceNotFoundException if doctor not found
     */
    @Transactional
    public DoctorProfileDTO updateProfile(String email, UpdateDoctorProfileRequest request) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with email: " + email
                ));

        // Update only non-null fields (partial update)
        if (request.getFirstName() != null) {
            doctor.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            doctor.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            doctor.setPhone(request.getPhone());
        }
        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization());
        }
        if (request.getLicenseNumber() != null) {
            doctor.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getQualification() != null) {
            doctor.setQualification(request.getQualification());
        }
        if (request.getYearsOfExperience() != null) {
            doctor.setYearsOfExperience(request.getYearsOfExperience());
        }
        if (request.getBio() != null) {
            doctor.setBio(request.getBio());
        }

        // Save and return updated profile
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return mapToDTO(updatedDoctor);
    }

    /**
     * Convert Doctor entity to DoctorProfileDTO.
     *
     * @param doctor doctor entity
     * @return doctor profile DTO
     */
    private DoctorProfileDTO mapToDTO(Doctor doctor) {
        return DoctorProfileDTO.builder()
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
                .build();
    }
}