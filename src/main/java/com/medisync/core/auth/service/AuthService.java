package com.medisync.core.auth.service;

import com.medisync.core.auth.dto.AuthResponse;
import com.medisync.core.auth.dto.LoginRequest;
import com.medisync.core.auth.dto.RegisterRequest;
import com.medisync.core.exception.InvalidCredentialsException;
import com.medisync.core.exception.UserAlreadyExistsException;
import com.medisync.core.user.entity.User;
import com.medisync.core.user.enums.Role;
import com.medisync.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.medisync.core.patient.entity.Patient;
import com.medisync.core.doctor.entity.Doctor;

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

    // Register a new user
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        User user;

        // Create Patient or Doctor based on role
        if (request.getRole() == Role.PATIENT) {
            user = Patient.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .isActive(true)
                    .build();
        } else if (request.getRole() == Role.DOCTOR) {
            user = Doctor.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .isActive(true)
                    .build();
        } else {
            user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .isActive(true)
                    .build();
        }

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // Authenticate user and generate JWT token
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
