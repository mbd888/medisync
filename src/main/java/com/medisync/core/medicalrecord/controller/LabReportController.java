package com.medisync.core.medicalrecord.controller;

import com.medisync.core.medicalrecord.dto.LabReportDTO;
import com.medisync.core.medicalrecord.service.LabReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for lab report endpoints.
 * Endpoints:
 * - POST /api/medical-records/{id}/lab-reports - Upload lab report (DOCTOR)
 * - GET /api/medical-records/{id}/lab-reports - List lab reports
 * - GET /api/lab-reports/{id}/download - Download lab report file
 */
@RestController
@RequiredArgsConstructor
public class LabReportController {

    private final LabReportService labReportService;

    /**
     * Upload a lab report file to a medical record.
     * POST /api/medical-records/{id}/lab-reports
     * Authorization: Bearer <doctor_token>
     * Content-Type: multipart/form-data
     * Form data:
     * - file: PDF/image file
     * - testName: Name of the test
     * - resultSummary: Optional summary
     *
     * @param id medical record ID
     * @param authentication Spring Security authentication
     * @param file uploaded file
     * @param testName name of the test
     * @param resultSummary optional summary of results
     * @return created lab report metadata
     * @throws IOException if file upload fails
     */
    @PostMapping("/api/medical-records/{id}/lab-reports")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<LabReportDTO> uploadLabReport(
            @PathVariable Long id,
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("testName") String testName,
            @RequestParam(value = "resultSummary", required = false) String resultSummary
    ) throws IOException {
        String doctorEmail = authentication.getName();
        LabReportDTO labReport = labReportService.uploadLabReport(
                id, file, testName, resultSummary, doctorEmail
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(labReport);
    }

    /**
     * Get all lab reports for a medical record.
     * GET /api/medical-records/{id}/lab-reports
     * Authorization: Bearer <token>
     *
     * @param id medical record ID
     * @return list of lab reports
     */
    @GetMapping("/api/medical-records/{id}/lab-reports")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<LabReportDTO>> getLabReports(@PathVariable Long id) {
        List<LabReportDTO> labReports = labReportService.getLabReports(id);
        return ResponseEntity.ok(labReports);
    }

    /**
     * Download a lab report file.
     * GET /api/lab-reports/{id}/download
     * Authorization: Bearer <token>
     * Returns the file with appropriate Content-Disposition header.
     *
     * @param id lab report ID
     * @param authentication Spring Security authentication
     * @return file as downloadable resource
     */
    @GetMapping("/api/lab-reports/{id}/download")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<Resource> downloadLabReport(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        Resource file = labReportService.downloadLabReport(id, userEmail);
        String filename = labReportService.getFileName(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(file);
    }
}
