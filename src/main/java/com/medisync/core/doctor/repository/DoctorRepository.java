package com.medisync.core.doctor.repository;

import com.medisync.core.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Doctor entity.
 * Spring Data JPA provides these methods automatically:
 * - save(doctor)
 * - findById(id)
 * - findAll()
 * - delete(doctor)
 * Custom methods are auto-implemented based on naming convention.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find a doctor by email address.
     *
     * @param email the email to search for
     * @return Optional containing the doctor if found, empty otherwise
     */
    Optional<Doctor> findByEmail(String email);

    /**
     * Check if a doctor exists with the given email.
     *
     * @param email the email to check
     * @return true if doctor exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a doctor by license number.
     *
     * @param licenseNumber the license number to search for
     * @return Optional containing the doctor if found, empty otherwise
     */
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}
