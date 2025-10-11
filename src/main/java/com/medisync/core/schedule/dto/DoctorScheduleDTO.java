package com.medisync.core.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for doctor schedule responses.
 * Returned when doctor views their schedule or after creating a schedule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleDTO {

    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDuration;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
