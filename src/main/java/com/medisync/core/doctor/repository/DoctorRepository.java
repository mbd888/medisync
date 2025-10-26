package com.medisync.core.doctor.repository;

import com.medisync.core.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository interface for Doctor entity
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}
