package com.medisync.core.exception;

/**
 * Exception thrown when a requested medical record is not found.
 * Returns 404 NOT FOUND status.
 */
public class MedicalRecordNotFoundException extends RuntimeException {

    public MedicalRecordNotFoundException(String message) {
        super(message);
    }
}
