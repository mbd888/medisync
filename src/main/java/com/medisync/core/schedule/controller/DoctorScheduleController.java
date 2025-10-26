package com.medisync.core.schedule.controller;

import com.medisync.core.schedule.dto.AvailableSlotDTO;
import com.medisync.core.schedule.dto.CreateScheduleRequest;
import com.medisync.core.schedule.dto.DoctorScheduleDTO;
import com.medisync.core.schedule.entity.DoctorSchedule;
import com.medisync.core.schedule.service.SchedulingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for doctor schedule management.
 * Endpoints:
 * - POST /api/doctors/schedule - Create a work schedule (DOCTOR only)
 * - GET /api/doctors/schedule - View my schedules (DOCTOR only)
 * - DELETE /api/doctors/schedule/{id} - Delete a schedule (DOCTOR only)
 * - GET /api/doctors/{doctorId}/available-slots - View available slots (PUBLIC)
 */
@RestController
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final SchedulingService schedulingService;

    // Create a new schedule for the current doctor
    @PostMapping("/api/doctors/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleDTO> createSchedule(
            Authentication authentication,
            @Valid @RequestBody CreateScheduleRequest request
    ) {
        String doctorEmail = authentication.getName();
        DoctorScheduleDTO schedule = schedulingService.createSchedule(doctorEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    // Get all schedules for the current doctor
    @GetMapping("/api/doctors/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DoctorScheduleDTO>> getMySchedules(Authentication authentication) {
        String doctorEmail = authentication.getName();
        List<DoctorScheduleDTO> schedules = schedulingService.getDoctorSchedules(doctorEmail);
        return ResponseEntity.ok(schedules);
    }

    // Delete a schedule for the current doctor
    @DeleteMapping("/api/doctors/schedule/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String doctorEmail = authentication.getName();
        schedulingService.deleteSchedule(id, doctorEmail);
        return ResponseEntity.noContent().build();
    }

    // Get available slots for a doctor
    @GetMapping("/api/doctors/{doctorId}/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<AvailableSlotDTO> slots = schedulingService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }

    // Get a doctor's schedule by ID
    @GetMapping("/api/doctors/{doctorId}/schedule")
    public ResponseEntity<List<DoctorScheduleDTO>> getDoctorSchedule(@PathVariable Long doctorId) {
        List<DoctorSchedule> schedules = schedulingService.getDoctorSchedulesByDoctorId(doctorId);

        List<DoctorScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> DoctorScheduleDTO.builder()
                        .id(schedule.getId())
                        .dayOfWeek(schedule.getDayOfWeek())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .slotDuration(schedule.getSlotDuration())
                        .isAvailable(schedule.getIsAvailable())
                        .createdAt(schedule.getCreatedAt())
                        .updatedAt(schedule.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(scheduleDTOs);
    }
}
