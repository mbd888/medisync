package com.medisync.core.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for booking a new appointment.
 * Patient sends this when booking:
 * POST /api/appointments
 * {
 *   "doctorId": 5,
 *   "appointmentDate": "2025-10-15",
 *   "startTime": "14:00",
 *   "reason": "Regular checkup"
 * }
 * Patient ID comes from JWT token (not in request body).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentRequest {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
