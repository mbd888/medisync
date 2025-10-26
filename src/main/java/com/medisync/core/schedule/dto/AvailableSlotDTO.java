package com.medisync.core.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for available time slot information.
 * Used when showing patients which times are available for booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotDTO {

    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
}
