package com.medisync.core.medicalrecord.repository;

import com.medisync.core.medicalrecord.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MedicalRecord entity.
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Find all medical records for a patient by their email.
     *
     * @param email patient's email
     * @return list of medical records
     */
    List<MedicalRecord> findByPatient_Email(String email);

    /**
     * Find all medical records created by a doctor by their email.
     *
     * @param email doctor's email
     * @return list of medical records
     */
    List<MedicalRecord> findByDoctor_Email(String email);

    /**
     * Find all medical records for a patient by patient ID.
     *
     * @param patientId patient's ID
     * @return list of medical records
     */
    List<MedicalRecord> findByPatient_Id(Long patientId);

    /**
     * Find all medical records created by a doctor by doctor ID.
     *
     * @param doctorId doctor's ID
     * @return list of medical records
     */
    List<MedicalRecord> findByDoctor_Id(Long doctorId);
}
