package com.medisync.core.appointment.dto;

import com.medisync.core.appointment.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Full DTO for appointment details.
 * Used when getting a specific appointment or after booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Appointment.AppointmentStatus status;
    private String reason;
    private String notes;

    private PatientInfo patient;
    private DoctorInfo doctor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested DTO for patient information in appointment.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    /**
     * Nested DTO for doctor information in appointment.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String specialization;
    }
}
