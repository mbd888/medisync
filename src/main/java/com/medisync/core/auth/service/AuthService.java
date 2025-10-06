package com.medisync.core.auth.service;

import com.medisync.core.auth.dto.AuthResponse;
import com.medisync.core.auth.dto.LoginRequest;
import com.medisync.core.auth.dto.RegisterRequest;
import com.medisync.core.exception.InvalidCredentialsException;
import com.medisync.core.exception.UserAlreadyExistsException;
import com.medisync.core.user.entity.User;
import com.medisync.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service handling authentication operations: registration and login.
 * Flow for registration:
 * 1. Check if email already exists
 * 2. Hash the password with BCrypt
 * 3. Save user to database
 * 4. Generate JWT token
 * 5. Return token + user info
 * Flow for login:
 * 1. Authenticate user (verify email + password)
 * 2. If valid, generate JWT token
 * 3. Return token + user info
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     * @param request registration details (email, password, role)
     * @return authentication response with JWT token
     * @throws UserAlreadyExistsException if email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        // Create new user with hashed password
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash the password
                .role(request.getRole())
                .isActive(true)
                .build();

        // Save to database
        userRepository.save(user);

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        // Return response
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * Authenticate and login a user.
     * @param request login credentials (email, password)
     * @return authentication response with JWT token
     * @throws InvalidCredentialsException if email doesn't exist or password is wrong
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user - this will throw exception if credentials are invalid
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            // For security: don't reveal if email exists or password is wrong
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Authentication was successful
        // Load the user from database
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        // Return response
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
