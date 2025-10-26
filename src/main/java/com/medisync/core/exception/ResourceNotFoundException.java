package com.medisync.core.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Used when:
 * - Patient profile not found
 * - Doctor profile not found
 * - Appointment not found
 * - Medical record not found
 * Returns 404 NOT FOUND status.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
