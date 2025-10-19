package com.medisync.core.appointment.service;

import com.medisync.core.appointment.dto.AppointmentDTO;
import com.medisync.core.appointment.dto.AppointmentListDTO;
import com.medisync.core.appointment.dto.BookAppointmentRequest;
import com.medisync.core.appointment.entity.Appointment;
import com.medisync.core.appointment.repository.AppointmentRepository;
import com.medisync.core.doctor.entity.Doctor;
import com.medisync.core.doctor.repository.DoctorRepository;
import com.medisync.core.exception.AppointmentNotFoundException;
import com.medisync.core.exception.ResourceNotFoundException;
import com.medisync.core.patient.entity.Patient;
import com.medisync.core.patient.repository.PatientRepository;
import com.medisync.core.schedule.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for appointment operations.
 * Handles:
 * - Booking appointments
 * - Getting appointments (for patients and doctors)
 * - Cancelling appointments
 * - Converting between Entity and DTO
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SchedulingService schedulingService;

    /**
     * Book a new appointment.
     *
     * @param patientEmail email of the patient booking (from JWT)
     * @param request booking details (doctor, date, time, reason)
     * @return created appointment DTO
     * @throws ResourceNotFoundException if patient or doctor not found
     */
    @Transactional
    public AppointmentDTO bookAppointment(String patientEmail, BookAppointmentRequest request) {
        // Find patient by email
        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with email: " + patientEmail
                ));

        // Find doctor by ID
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.getDoctorId()
                ));

        // Calculate end time (default 30 minutes)
        LocalTime endTime = request.getStartTime().plusMinutes(30);

        // VALIDATION: Check if doctor is available at this time
        schedulingService.isDoctorAvailable(
                doctor.getId(),
                request.getAppointmentDate(),
                request.getStartTime()
        );

        // VALIDATION: Check for scheduling conflicts
        schedulingService.hasConflict(
                doctor.getId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                endTime
        );

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .reason(request.getReason())
                .build();

        // Save and return
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToFullDTO(savedAppointment);
    }

    /**
     * Get all appointments for a patient.
     *
     * @param patientEmail patient's email
     * @return list of appointments
     */
    @Transactional(readOnly = true)
    public List<AppointmentListDTO> getPatientAppointments(String patientEmail) {
        List<Appointment> appointments = appointmentRepository.findByPatient_Email(patientEmail);
        return appointments.stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all appointments for a doctor.
     *
     * @param doctorEmail doctor's email
     * @return list of appointments
     */
    @Transactional(readOnly = true)
    public List<AppointmentListDTO> getDoctorAppointments(String doctorEmail) {
        List<Appointment> appointments = appointmentRepository.findByDoctor_Email(doctorEmail);
        return appointments.stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific appointment by ID.
     *
     * @param id appointment ID
     * @param userEmail email of the user requesting (for security check)
     * @return appointment DTO
     * @throws AppointmentNotFoundException if appointment not found
     * @throws SecurityException if user doesn't have access to this appointment
     */
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found with id: " + id
                ));

        // Security check: user must be either the patient or the doctor
        boolean isPatient = appointment.getPatient().getEmail().equals(userEmail);
        boolean isDoctor = appointment.getDoctor().getEmail().equals(userEmail);

        if (!isPatient && !isDoctor) {
            throw new SecurityException("You don't have access to this appointment");
        }

        return mapToFullDTO(appointment);
    }

    /**
     * Cancel an appointment.
     *
     * @param id appointment ID
     * @param userEmail email of the user cancelling
     * @return cancelled appointment DTO
     * @throws AppointmentNotFoundException if appointment not found
     * @throws SecurityException if user doesn't have permission to cancel
     */
    @Transactional
    public AppointmentDTO cancelAppointment(Long id, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found with id: " + id
                ));

        // Security check: user must be either the patient or the doctor
        boolean isPatient = appointment.getPatient().getEmail().equals(userEmail);
        boolean isDoctor = appointment.getDoctor().getEmail().equals(userEmail);

        if (!isPatient && !isDoctor) {
            throw new SecurityException("You don't have permission to cancel this appointment");
        }

        // Update status to CANCELLED
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        return mapToFullDTO(cancelledAppointment);
    }

    /**
     * Convert Appointment entity to full AppointmentDTO.
     *
     * @param appointment appointment entity
     * @return appointment DTO with all details
     */
    private AppointmentDTO mapToFullDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .patient(AppointmentDTO.PatientInfo.builder()
                        .id(appointment.getPatient().getId())
                        .firstName(appointment.getPatient().getFirstName())
                        .lastName(appointment.getPatient().getLastName())
                        .email(appointment.getPatient().getEmail())
                        .phone(appointment.getPatient().getPhone())
                        .build())
                .doctor(AppointmentDTO.DoctorInfo.builder()
                        .id(appointment.getDoctor().getId())
                        .firstName(appointment.getDoctor().getFirstName())
                        .lastName(appointment.getDoctor().getLastName())
                        .email(appointment.getDoctor().getEmail())
                        .specialization(appointment.getDoctor().getSpecialization())
                        .build())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    /**
     * Convert Appointment entity to simplified AppointmentListDTO.
     *
     * @param appointment appointment entity
     * @return simplified appointment DTO for lists
     */
    private AppointmentListDTO mapToListDTO(Appointment appointment) {
        String patientFirstName = appointment.getPatient().getFirstName() != null ?
                appointment.getPatient().getFirstName() : "Unknown";
        String patientLastName = appointment.getPatient().getLastName() != null ?
                appointment.getPatient().getLastName() : "";
        String patientName = patientFirstName + " " + patientLastName;

        String doctorFirstName = appointment.getDoctor().getFirstName() != null ?
                appointment.getDoctor().getFirstName() : "Unknown";
        String doctorLastName = appointment.getDoctor().getLastName() != null ?
                appointment.getDoctor().getLastName() : "";
        String doctorName = "Dr. " + doctorFirstName + " " + doctorLastName;

        return AppointmentListDTO.builder()
                .id(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .patientName(patientName)
                .doctorName(doctorName)
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .build();
    }
}
