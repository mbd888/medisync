package com.medisync.core.medicalrecord.service;

import com.medisync.core.exception.MedicalRecordNotFoundException;
import com.medisync.core.exception.ResourceNotFoundException;
import com.medisync.core.medicalrecord.dto.LabReportDTO;
import com.medisync.core.medicalrecord.entity.LabReport;
import com.medisync.core.medicalrecord.entity.MedicalRecord;
import com.medisync.core.medicalrecord.repository.LabReportRepository;
import com.medisync.core.medicalrecord.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for lab report operations.
 * Handles:
 * - Uploading lab report files
 * - Downloading lab report files
 * - Viewing lab reports
 * File storage: Local file system (uploads/lab-reports/)
 * In production, use cloud storage like AWS S3.
 */
@Service
@RequiredArgsConstructor
public class LabReportService {

    private final LabReportRepository labReportRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Value("${file.upload-dir:uploads/lab-reports}")
    private String uploadDir;

    // Upload a lab report file
    @Transactional
    public LabReportDTO uploadLabReport(
            Long medicalRecordId,
            MultipartFile file,
            String testName,
            String resultSummary,
            String doctorEmail
    ) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + medicalRecordId
                ));

        if (!medicalRecord.getDoctor().getEmail().equals(doctorEmail)) {
            throw new SecurityException("You can only upload files to your own medical records");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        LabReport labReport = LabReport.builder()
                .medicalRecord(medicalRecord)
                .testName(testName)
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .resultSummary(resultSummary)
                .build();

        LabReport savedReport = labReportRepository.save(labReport);
        return mapToDTO(savedReport);
    }

    // Download a lab report file
    @Transactional(readOnly = true)
    public Resource downloadLabReport(Long labReportId, String userEmail) {

        LabReport labReport = labReportRepository.findById(labReportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab report not found with id: " + labReportId
                ));

        MedicalRecord record = labReport.getMedicalRecord();
        boolean isPatient = record.getPatient().getEmail().equals(userEmail);
        boolean isDoctor = record.getDoctor().getEmail().equals(userEmail);

        if (!isPatient && !isDoctor) {
            throw new SecurityException("You don't have access to this lab report");
        }

        try {
            Path filePath = Paths.get(labReport.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or not readable");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error loading file: " + e.getMessage());
        }
    }

    // Get all lab reports for a medical record
    @Transactional(readOnly = true)
    public List<LabReportDTO> getLabReports(Long medicalRecordId) {
        List<LabReport> labReports = labReportRepository.findByMedicalRecord_Id(medicalRecordId);
        return labReports.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get the file name of a lab report
    public String getFileName(Long labReportId) {
        LabReport labReport = labReportRepository.findById(labReportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lab report not found with id: " + labReportId
                ));
        return labReport.getFileName();
    }

    // Convert a lab report entity to a DTO
    private LabReportDTO mapToDTO(LabReport labReport) {
        return LabReportDTO.builder()
                .id(labReport.getId())
                .testName(labReport.getTestName())
                .fileName(labReport.getFileName())
                .fileType(labReport.getFileType())
                .fileSize(labReport.getFileSize())
                .resultSummary(labReport.getResultSummary())
                .uploadedAt(labReport.getUploadedAt())
                .downloadUrl("/api/lab-reports/" + labReport.getId() + "/download")
                .build();
    }
}
