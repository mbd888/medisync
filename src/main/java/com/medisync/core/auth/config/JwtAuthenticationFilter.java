package com.medisync.core.auth.config;

import com.medisync.core.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 1. Extracts JWT token from Authorization header
 * 2. Validates the token
 * 3. Loads the user from database
 * 4. Sets authentication in SecurityContext
 * Flow:
 * Request → Filter checks token → If valid, set authentication → Continue to Controller
 *                               → If invalid, return 401
 * Authorization header format:
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extract Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // If no Authorization header or doesn't start with "Bearer ", skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract JWT token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);

        // 3. Extract email from token
        userEmail = jwtService.extractUsername(jwt);

        // 4. If email exists and user is not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Validate token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set additional details
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 6. Set authentication in SecurityContext
                // This tells Spring Security: "This user is authenticated"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continue the filter chain (pass request to next filter/controller)
        filterChain.doFilter(request, response);
    }
}
