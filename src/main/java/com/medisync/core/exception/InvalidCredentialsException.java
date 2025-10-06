package com.medisync.core.exception;

/**
 * Exception thrown when login credentials are invalid.
 * This could mean:
 * - User with email doesn't exist
 * - Password is incorrect
 * Usage in AuthService:
 * if (user not found || password wrong) {
 *     throw new InvalidCredentialsException("Invalid email or password");
 * }
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
