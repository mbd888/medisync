package com.medisync.core.medicalrecord.repository;

import com.medisync.core.medicalrecord.entity.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for LabReport entity.
 */
@Repository
public interface LabReportRepository extends JpaRepository<LabReport, Long> {

    /**
     * Find all lab reports for a specific medical record.
     *
     * @param medicalRecordId medical record ID
     * @return list of lab reports
     */
    List<LabReport> findByMedicalRecord_Id(Long medicalRecordId);
}
