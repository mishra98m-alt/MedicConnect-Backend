package com.medicconnect.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Utility class for generating and validating JWT tokens
 * used specifically for user authentication sessions.
 */
@Component
public class AuthTokenUtil {

    @Value("${jwt.auth.secret}")
    private String secret;

    @Value("${jwt.auth.expiration}")
    private long expirationTime;

    /**
     * Generate signing key for JWT using HS256 algorithm.
     */
    private Key getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    // 🔒 Ensure key is at least 32 bytes (256 bits) — required by HS256
    if (keyBytes.length < 32) {
        System.out.println("⚠️ JWT auth secret too short! Padding automatically to 256 bits.");
        byte[] padded = new byte[32];
        System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
        keyBytes = padded;
    }
    return Keys.hmacShaKeyFor(keyBytes);
}


    /**
     * Generate a JWT token containing user-specific claims.
     *
     * @param claims - Map of claims (like userId, email, roles)
     * @return signed JWT token as String
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token and return its claims if valid.
     *
     * @param token - JWT token string
     * @return Claims if valid, otherwise null
     */
    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Auth token expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("❌ Invalid auth token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract a specific claim from token.
     *
     * @param token     - JWT token
     * @param claimKey  - claim key (e.g. "email")
     * @return claim value or null if token invalid
     */
    public Object getClaim(String token, String claimKey) {
        Claims claims = validateAndGetClaims(token);
        return (claims != null) ? claims.get(claimKey) : null;
    }
}
