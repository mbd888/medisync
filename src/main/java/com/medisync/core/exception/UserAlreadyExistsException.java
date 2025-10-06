package com.medisync.core.exception;

/**
 * Exception thrown when attempting to register with an email that already exists.
 * Usage in AuthService:
 * if (userRepository.existsByEmail(email)) {
 *     throw new UserAlreadyExistsException("User with email " + email + " already exists");
 * }
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
