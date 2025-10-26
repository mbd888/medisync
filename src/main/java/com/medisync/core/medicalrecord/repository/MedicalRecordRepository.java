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

    List<MedicalRecord> findByPatient_Email(String email);
    List<MedicalRecord> findByDoctor_Email(String email);

    List<MedicalRecord> findByPatient_Id(Long patientId);
    List<MedicalRecord> findByDoctor_Id(Long doctorId);
}
