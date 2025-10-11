package com.medisync.core.schedule.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO for creating a doctor's work schedule.
 * Doctor sends this to set their working hours:
 * POST /api/doctors/schedule
 * {
 *   "dayOfWeek": "MONDAY",
 *   "startTime": "09:00",
 *   "endTime": "17:00",
 *   "slotDuration": 30
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Slot duration is required")
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    private Integer slotDuration; // In minutes (typically 30)
}
