package com.medisync.core.appointment.dto;

import com.medisync.core.appointment.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Simplified DTO for listing appointments.
 * Used when getting a list of appointments.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListDTO {

    private Long id;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Appointment.AppointmentStatus status;
    private String reason;

    // Simple patient info (just name)
    private String patientName;

    // Simple doctor info (just name and specialization)
    private String doctorName;
    private String doctorSpecialization;
}
