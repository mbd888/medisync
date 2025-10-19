package com.medisync.core.medicalrecord.service;

import com.medisync.core.appointment.entity.Appointment;
import com.medisync.core.appointment.repository.AppointmentRepository;
import com.medisync.core.doctor.entity.Doctor;
import com.medisync.core.doctor.repository.DoctorRepository;
import com.medisync.core.exception.MedicalRecordNotFoundException;
import com.medisync.core.exception.ResourceNotFoundException;
import com.medisync.core.medicalrecord.dto.*;
import com.medisync.core.medicalrecord.entity.LabReport;
import com.medisync.core.medicalrecord.entity.MedicalRecord;
import com.medisync.core.medicalrecord.entity.Prescription;
import com.medisync.core.medicalrecord.repository.LabReportRepository;
import com.medisync.core.medicalrecord.repository.MedicalRecordRepository;
import com.medisync.core.medicalrecord.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for medical record operations.
 * Handles:
 * - Creating medical records (after appointments)
 * - Viewing medical records (for patients and doctors)
 * - Converting between Entity and DTO
 */
@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;

    /**
     * Create a medical record for an appointment.
     *
     * @param doctorEmail email of the doctor creating the record (from JWT)
     * @param request medical record details
     * @return created medical record DTO
     * @throws ResourceNotFoundException if appointment or doctor not found
     */
    @Transactional
    public MedicalRecordDTO createMedicalRecord(String doctorEmail, CreateMedicalRecordRequest request) {
        // Find doctor
        Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with email: " + doctorEmail
                ));

        // Find appointment
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + request.getAppointmentId()
                ));

        // Validate: appointment must belong to this doctor
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("You can only create records for your own appointments");
        }

        // Create medical record
        MedicalRecord medicalRecord = MedicalRecord.builder()
                .patient(appointment.getPatient())
                .doctor(doctor)
                .appointment(appointment)
                .visitDate(appointment.getAppointmentDate())
                .diagnosis(request.getDiagnosis())
                .symptoms(request.getSymptoms())
                .notes(request.getNotes())
                .followUpDate(request.getFollowUpDate())
                .build();

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

        // Update appointment status to COMPLETED
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return mapToFullDTO(savedRecord);
    }

    /**
     * Get all medical records for a patient.
     *
     * @param patientEmail patient's email
     * @return list of medical records
     */
    @Transactional(readOnly = true)
    public List<MedicalRecordListDTO> getPatientMedicalRecords(String patientEmail) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatient_Email(patientEmail);
        return records.stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all medical records created by a doctor.
     *
     * @param doctorEmail doctor's email
     * @return list of medical records
     */
    @Transactional(readOnly = true)
    public List<MedicalRecordListDTO> getDoctorMedicalRecords(String doctorEmail) {
        List<MedicalRecord> records = medicalRecordRepository.findByDoctor_Email(doctorEmail);
        return records.stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific medical record by ID.
     *
     * @param id medical record ID
     * @param userEmail email of user requesting (for security check)
     * @return medical record DTO
     * @throws MedicalRecordNotFoundException if record not found
     */
    @Transactional(readOnly = true)
    public MedicalRecordDTO getMedicalRecordById(Long id, String userEmail) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + id
                ));

        // Security check: user must be either the patient or the doctor
        boolean isPatient = record.getPatient().getEmail().equals(userEmail);
        boolean isDoctor = record.getDoctor().getEmail().equals(userEmail);

        if (!isPatient && !isDoctor) {
            throw new SecurityException("You don't have access to this medical record");
        }

        return mapToFullDTO(record);
    }

    /**
     * Convert MedicalRecord entity to full DTO.
     */
    private MedicalRecordDTO mapToFullDTO(MedicalRecord record) {
        // Get prescriptions
        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecord_Id(record.getId());
        List<PrescriptionDTO> prescriptionDTOs = prescriptions.stream()
                .map(this::mapPrescriptionToDTO)
                .collect(Collectors.toList());

        // Get lab reports
        List<LabReport> labReports = labReportRepository.findByMedicalRecord_Id(record.getId());
        List<LabReportDTO> labReportDTOs = labReports.stream()
                .map(this::mapLabReportToDTO)
                .collect(Collectors.toList());

        return MedicalRecordDTO.builder()
                .id(record.getId())
                .visitDate(record.getVisitDate())
                .diagnosis(record.getDiagnosis())
                .symptoms(record.getSymptoms())
                .notes(record.getNotes())
                .followUpDate(record.getFollowUpDate())
                .patient(MedicalRecordDTO.PatientInfo.builder()
                        .id(record.getPatient().getId())
                        .firstName(record.getPatient().getFirstName())
                        .lastName(record.getPatient().getLastName())
                        .email(record.getPatient().getEmail())
                        .build())
                .doctor(MedicalRecordDTO.DoctorInfo.builder()
                        .id(record.getDoctor().getId())
                        .firstName(record.getDoctor().getFirstName())
                        .lastName(record.getDoctor().getLastName())
                        .specialization(record.getDoctor().getSpecialization())
                        .build())
                .prescriptions(prescriptionDTOs)
                .labReports(labReportDTOs)
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    /**
     * Convert MedicalRecord entity to simplified list DTO.
     */
    private MedicalRecordListDTO mapToListDTO(MedicalRecord record) {
        String doctorName = "Dr. " + record.getDoctor().getFirstName() + " " +
                record.getDoctor().getLastName();

        // Count prescriptions and lab reports
        int prescriptionCount = prescriptionRepository.findByMedicalRecord_Id(record.getId()).size();
        int labReportCount = labReportRepository.findByMedicalRecord_Id(record.getId()).size();

        return MedicalRecordListDTO.builder()
                .id(record.getId())
                .visitDate(record.getVisitDate())
                .diagnosis(record.getDiagnosis())
                .doctorName(doctorName)
                .doctorSpecialization(record.getDoctor().getSpecialization())
                .followUpDate(record.getFollowUpDate())
                .prescriptionCount(prescriptionCount)
                .labReportCount(labReportCount)
                .build();
    }

    /**
     * Convert Prescription entity to DTO.
     */
    private PrescriptionDTO mapPrescriptionToDTO(Prescription prescription) {
        return PrescriptionDTO.builder()
                .id(prescription.getId())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .duration(prescription.getDuration())
                .instructions(prescription.getInstructions())
                .createdAt(prescription.getCreatedAt())
                .build();
    }

    /**
     * Convert LabReport entity to DTO.
     */
    private LabReportDTO mapLabReportToDTO(LabReport labReport) {
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
