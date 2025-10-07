package com.medisync.core.patient.repository;

import com.medisync.core.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Patient entity.
 * Spring Data JPA provides these methods automatically:
 * - save(patient)
 * - findById(id)
 * - findAll()
 * - delete(patient)
 * Custom methods are auto-implemented based on naming convention.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by email address.
     *
     * @param email the email to search for
     * @return Optional containing the patient if found, empty otherwise
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Check if a patient exists with the given email.
     *
     * @param email the email to check
     * @return true if patient exists, false otherwise
     */
    boolean existsByEmail(String email);
}
