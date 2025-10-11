package com.medisync.core.exception;

/**
 * Exception thrown when trying to book an appointment when doctor is not available.
 * Can happen when:
 * - Doctor doesn't work on that day
 * - Time is outside doctor's working hours
 * - Doctor marked that day as unavailable (time off)
 * Returns 400 BAD REQUEST status.
 * Usage:
 * if (doctor not working at this time) {
 *     throw new DoctorNotAvailableException("Doctor is not available at this time");
 * }
 */
public class DoctorNotAvailableException extends RuntimeException {

    public DoctorNotAvailableException(String message) {
        super(message);
    }
}
