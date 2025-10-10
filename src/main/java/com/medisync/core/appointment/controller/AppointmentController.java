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
 * Endpoints:
 * - POST /api/appointments - Book a new appointment (PATIENT only)
 * - GET /api/appointments - Get all my appointments (PATIENT or DOCTOR)
 * - GET /api/appointments/{id} - Get specific appointment details
 * - DELETE /api/appointments/{id} - Cancel an appointment
 * Security:
 * - Booking requires PATIENT role
 * - Viewing appointments available to both PATIENT and DOCTOR
 * - Users can only access their own appointments
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Book a new appointment.
     * POST /api/appointments
     * Authorization: Bearer <patient_token>
     * Content-Type: application/json
     * Request body:
     * {
     *   "doctorId": 5,
     *   "appointmentDate": "2025-10-15",
     *   "startTime": "14:00",
     *   "reason": "Regular checkup"
     * }
     * Response (201 CREATED):
     * {
     *   "id": 1,
     *   "appointmentDate": "2025-10-15",
     *   "startTime": "14:00",
     *   "endTime": "14:30",
     *   "status": "SCHEDULED",
     *   "patient": { ... },
     *   "doctor": { ... },
     *   ...
     * }
     *
     * @param authentication Spring Security authentication object
     * @param request booking request details
     * @return created appointment
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
     * Authorization: Bearer <token>
     * - If PATIENT: returns patient's appointments
     * - If DOCTOR: returns doctor's appointments
     * Response (200 OK):
     * [
     *   {
     *     "id": 1,
     *     "appointmentDate": "2025-10-15",
     *     "startTime": "14:00",
     *     "status": "SCHEDULED",
     *     "patientName": "John Doe",
     *     "doctorName": "Dr. Jane Smith",
     *     ...
     *   },
     *   ...
     * ]
     *
     * @param authentication Spring Security authentication object
     * @return list of appointments
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<AppointmentListDTO>> getMyAppointments(Authentication authentication) {
        String userEmail = authentication.getName();

        // Check user's role to determine which appointments to fetch
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
     * Authorization: Bearer <token>
     * Response (200 OK):
     * {
     *   "id": 1,
     *   "appointmentDate": "2025-10-15",
     *   "startTime": "14:00",
     *   "endTime": "14:30",
     *   "status": "SCHEDULED",
     *   "reason": "Regular checkup",
     *   "patient": {
     *     "id": 2,
     *     "firstName": "John",
     *     "lastName": "Doe",
     *     ...
     *   },
     *   "doctor": {
     *     "id": 5,
     *     "firstName": "Jane",
     *     "lastName": "Smith",
     *     "specialization": "Cardiology",
     *     ...
     *   },
     *   ...
     * }
     *
     * @param id appointment ID
     * @param authentication Spring Security authentication object
     * @return appointment details
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
     * Authorization: Bearer <token>
     * Changes appointment status to CANCELLED.
     * Only the patient or doctor involved can cancel.
     * Response (200 OK):
     * {
     *   "id": 1,
     *   "status": "CANCELLED",
     *   ...
     * }
     *
     * @param id appointment ID
     * @param authentication Spring Security authentication object
     * @return cancelled appointment
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
