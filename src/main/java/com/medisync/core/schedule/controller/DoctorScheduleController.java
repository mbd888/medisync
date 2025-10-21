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

    /**
     * Create a work schedule for the current doctor.
     * POST /api/doctors/schedule
     * Authorization: Bearer <doctor_token>
     * Content-Type: application/json
     * Request body:
     * {
     *   "dayOfWeek": "MONDAY",
     *   "startTime": "09:00",
     *   "endTime": "17:00",
     *   "slotDuration": 30
     * }
     * Response (201 CREATED):
     * {
     *   "id": 1,
     *   "dayOfWeek": "MONDAY",
     *   "startTime": "09:00",
     *   "endTime": "17:00",
     *   "slotDuration": 30,
     *   "isAvailable": true,
     *   ...
     * }
     *
     * @param authentication Spring Security authentication object
     * @param request schedule details
     * @return created schedule
     */
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

    /**
     * Get all schedules for the current doctor.
     * GET /api/doctors/schedule
     * Authorization: Bearer <doctor_token>
     * Response (200 OK):
     * [
     *   {
     *     "id": 1,
     *     "dayOfWeek": "MONDAY",
     *     "startTime": "09:00",
     *     "endTime": "17:00",
     *     ...
     *   },
     *   ...
     * ]
     *
     * @param authentication Spring Security authentication object
     * @return list of schedules
     */
    @GetMapping("/api/doctors/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DoctorScheduleDTO>> getMySchedules(Authentication authentication) {
        String doctorEmail = authentication.getName();
        List<DoctorScheduleDTO> schedules = schedulingService.getDoctorSchedules(doctorEmail);
        return ResponseEntity.ok(schedules);
    }

    /**
     * Delete a schedule.
     * DELETE /api/doctors/schedule/{id}
     * Authorization: Bearer <doctor_token>
     * Response (204 NO CONTENT)
     *
     * @param id schedule ID
     * @param authentication Spring Security authentication object
     */
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

    /**
     * Get available time slots for a specific doctor on a specific date.
     * GET /api/doctors/{doctorId}/available-slots?date=2025-10-15
     * Response (200 OK):
     * [
     *   {
     *     "startTime": "09:00",
     *     "endTime": "09:30",
     *     "isAvailable": true
     *   },
     *   {
     *     "startTime": "09:30",
     *     "endTime": "10:00",
     *     "isAvailable": false
     *   },
     *   ...
     * ]
     *
     * @param doctorId doctor's ID
     * @param date the date to check (format: yyyy-MM-dd)
     * @return list of time slots with availability
     */
    @GetMapping("/api/doctors/{doctorId}/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<AvailableSlotDTO> slots = schedulingService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }

    /**
     * Get doctor's schedule (public endpoint for patients to see working days).
     * GET /api/doctors/{doctorId}/schedule
     */
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
