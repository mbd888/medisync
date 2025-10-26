package com.medisync.core.exception;

/**
 * Exception thrown when attempting to register with an email that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
