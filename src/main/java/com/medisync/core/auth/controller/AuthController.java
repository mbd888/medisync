package com.medisync.core.auth.controller;

import com.medisync.core.auth.dto.AuthResponse;
import com.medisync.core.auth.dto.LoginRequest;
import com.medisync.core.auth.dto.RegisterRequest;
import com.medisync.core.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 * Endpoints:
 * - POST /api/auth/register - Register a new user
 * - POST /api/auth/login - Login with email and password
 * All endpoints return AuthResponse with JWT token on success.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user.
     * POST /api/auth/register
     * Request body:
     * {
     *   "email": "patient@test.com",
     *   "password": "password123",
     *   "role": "PATIENT"
     * }
     * Response (201 Created):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "email": "patient@test.com",
     *   "role": "PATIENT"
     * }
     *
     * @param request registration details
     * @return authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login with email and password.
     * POST /api/auth/login
     * Request body:
     * {
     *   "email": "patient@test.com",
     *   "password": "password123"
     * }
     * Response (200 OK):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "email": "patient@test.com",
     *   "role": "PATIENT"
     * }
     *
     * @param request login credentials
     * @return authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
