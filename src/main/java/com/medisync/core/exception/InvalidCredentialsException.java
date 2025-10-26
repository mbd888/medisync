package com.medisync.core.exception;

/**
 * Exception thrown when login credentials are invalid.
 * This could mean:
 * - User with email doesn't exist
 * - Password is incorrect
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
