package com.medisync.core.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT token operations: generation, validation, and extraction.
 * JWT (JSON Web Token) structure:
 * Header.Payload.Signature
 * Example token:
 * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwYXRpZW50QHRlc3QuY29tIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
 * The payload contains:
 * - sub: subject (user's email)
 * - iat: issued at (timestamp)
 * - exp: expiration (timestamp)
 */
@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extract the username (email) from the token.
     *
     * @param token JWT token
     * @return email stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a specific claim from the token.
     *
     * @param token JWT token
     * @param claimsResolver function to extract the desired claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate a token for a user with no extra claims.
     *
     * @param userDetails user to generate token for
     * @return JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a token with extra claims.
     *
     * @param extraClaims additional data to store in token
     * @param userDetails user to generate token for
     * @return JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Build the actual JWT token.
     *
     * @param extraClaims additional claims to include
     * @param userDetails user details
     * @param expiration token lifetime in milliseconds
     * @return signed JWT token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate if the token is valid for the given user.
     * Checks:
     * 1. Username in token matches the user
     * 2. Token is not expired
     *
     * @param token JWT token
     * @param userDetails user to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check if token is expired.
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract the expiration date from token.
     *
     * @param token JWT token
     * @return expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract all claims from the token.
     * Parses and verifies the token signature.
     *
     * @param token JWT token
     * @return all claims in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get the signing key from the secret.
     * Decodes the base64-encoded secret key.
     *
     * @return signing key for HMAC-SHA256
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
