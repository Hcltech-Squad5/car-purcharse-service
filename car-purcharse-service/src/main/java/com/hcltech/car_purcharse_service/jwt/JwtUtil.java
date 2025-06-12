package com.hcltech.car_purcharse_service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function; // Added for claims extraction

@Service
public class JwtUtil {

    @Value("${spring.app.jwt.secret}")
    private String SECRET_KEY; // Ensure this is a strong, base64-encoded key

    @Value("${spring.app.jwt.expiration-ms}")
    private long JWT_EXPIRATION; // This is the milliseconds for token validity, NOT an absolute date

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    // Helper method to extract a single claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        // This method will throw various JWT exceptions (ExpiredJwtException, SignatureException, etc.)
        // if the token is invalid or expired. These should be caught in JwtFilter.
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        final long now = System.currentTimeMillis();
        // JWT_EXPIRATION should be added to `now` to get the future expiration date
        final long expirationMillis = now + JWT_EXPIRATION;

        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(expirationMillis)) // Corrected: expiration is `now + duration`
                .claims(claims)
                .signWith(getSignKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // You can add roles or other user details as claims here if needed
        // For example: claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Added trim() for robustness against leading/trailing spaces
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    public String getUserNameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // *** CRITICAL FIX: Corrected validateToken logic ***
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUserNameFromToken(token);
        // Check if username matches AND if the token is NOT expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpirated(token));
    }

    public Boolean isTokenExpirated(String token) {
        // This method now correctly checks if the token's expiration date is BEFORE the current date
        return getExpirationDateFromToken(token).before(new Date());
    }
}