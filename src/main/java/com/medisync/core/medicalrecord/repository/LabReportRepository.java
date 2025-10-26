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

    List<LabReport> findByMedicalRecord_Id(Long medicalRecordId);
}
