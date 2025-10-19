package com.medisync.core.medicalrecord.repository;

import com.medisync.core.medicalrecord.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Prescription entity.
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Find all prescriptions for a specific medical record.
     *
     * @param medicalRecordId medical record ID
     * @return list of prescriptions
     */
    List<Prescription> findByMedicalRecord_Id(Long medicalRecordId);
}
