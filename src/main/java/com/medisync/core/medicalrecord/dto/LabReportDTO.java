package com.medisync.core.medicalrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for lab report details.
 * Used when viewing lab reports in a medical record.
 * Contains metadata about the uploaded file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabReportDTO {

    private Long id;
    private String testName;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String resultSummary;
    private LocalDateTime uploadedAt;
    private String downloadUrl; // URL to download the file
}
