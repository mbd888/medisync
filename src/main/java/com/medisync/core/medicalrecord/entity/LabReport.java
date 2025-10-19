package com.medisync.core.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * LabReport entity representing an uploaded test result or medical report.
 * Belongs to a MedicalRecord.
 * Stores file information for uploaded documents (blood tests, X-rays, etc.)
 * File storage:
 * - Development: Local file system
 * - Production: AWS S3 (or similar cloud storage)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lab_reports")
@EntityListeners(AuditingEntityListener.class)
public class LabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName; // e.g., "Blood Test", "X-Ray Chest", "MRI Brain"

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName; // Original file name

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath; // Where file is stored on disk/cloud

    @Column(name = "file_type", length = 50)
    private String fileType; // e.g., "application/pdf", "image/jpeg"

    @Column(name = "file_size")
    private Long fileSize; // Size in bytes

    @Column(name = "result_summary", length = 1000)
    private String resultSummary; // Optional text summary of results

    @CreatedDate
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
}
