package com.medicconnect.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Utility class for generating and validating JWT tokens.
 * Used for secure prefilled registration links and other token-based operations.
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.prefilled.secret}")
    private String secret;

    @Value("${jwt.prefilled.expiration}")
    private long expirationTime;

    /**
     * Generate the signing key from the secret string.
     */
    private Key getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    // 🔒 Ensure key is at least 32 bytes (256 bits)
    if (keyBytes.length < 32) {
        System.out.println("⚠️ JWT prefilled secret too short! Padding automatically to 256 bits.");
        byte[] padded = new byte[32];
        System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
        keyBytes = padded;
    }
    return Keys.hmacShaKeyFor(keyBytes);
}


    /**
     * Generate a JWT token with custom claims (like orgId, email, etc.)
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
     * Validate and parse a JWT token.
     */
    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Prefilled token expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("❌ Invalid prefilled token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract a single claim value.
     */
    public Object getClaim(String token, String claimKey) {
        Claims claims = validateAndGetClaims(token);
        return (claims != null) ? claims.get(claimKey) : null;
    }
}
