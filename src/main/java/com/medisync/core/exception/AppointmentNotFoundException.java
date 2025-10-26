package com.medisync.core.exception;

/**
 * Exception thrown when a requested appointment is not found.
 * Returns 404 NOT FOUND status.
 */
public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
