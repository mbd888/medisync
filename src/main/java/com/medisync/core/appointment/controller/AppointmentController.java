package com.medisync.core.appointment.controller;

import com.medisync.core.appointment.dto.AppointmentDTO;
import com.medisync.core.appointment.dto.AppointmentListDTO;
import com.medisync.core.appointment.dto.BookAppointmentRequest;
import com.medisync.core.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for appointment endpoints.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Book a new appointment.
     * POST /api/appointments
     */
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            Authentication authentication,
            @Valid @RequestBody BookAppointmentRequest request
    ) {
        String patientEmail = authentication.getName();
        AppointmentDTO appointment = appointmentService.bookAppointment(patientEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    /**
     * Get all appointments for the current user.
     * GET /api/appointments
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<AppointmentListDTO>> getMyAppointments(Authentication authentication) {
        String userEmail = authentication.getName();

        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        List<AppointmentListDTO> appointments;
        if (isPatient) {
            appointments = appointmentService.getPatientAppointments(userEmail);
        } else {
            appointments = appointmentService.getDoctorAppointments(userEmail);
        }

        return ResponseEntity.ok(appointments);
    }

    /**
     * Get a specific appointment by ID.
     * GET /api/appointments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<AppointmentDTO> getAppointmentById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        AppointmentDTO appointment = appointmentService.getAppointmentById(id, userEmail);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Cancel an appointment.
     * DELETE /api/appointments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<AppointmentDTO> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id, userEmail);
        return ResponseEntity.ok(cancelledAppointment);
    }
}
