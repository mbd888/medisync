package com.medisync.core.exception;

/**
 * Exception thrown when trying to book an appointment at a time that's already taken.
 * Returns 409 CONFLICT status.
 * Usage:
 * if (time slot already booked) {
 *     throw new ScheduleConflictException("Time slot already booked for this doctor");
 * }
 */
public class ScheduleConflictException extends RuntimeException {

    public ScheduleConflictException(String message) {
        super(message);
    }
}
