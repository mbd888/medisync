package com.medisync.core.exception;

/**
 * Exception thrown when a requested medical record is not found.
 * Returns 404 NOT FOUND status.
 * Usage:
 * if (medical record not found) {
 *     throw new MedicalRecordNotFoundException("Medical record not found with id: " + id);
 * }
 */
public class MedicalRecordNotFoundException extends RuntimeException {

    public MedicalRecordNotFoundException(String message) {
        super(message);
    }
}
