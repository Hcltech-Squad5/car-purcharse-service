package com.hcltech.car_purcharse_service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the JwtUtil class.
 * These tests focus on verifying the functionality of JWT token generation,
 * parsing, validation, and extraction methods in isolation.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class JwtUtilTest {

    // The class under test
    @InjectMocks
    private JwtUtil jwtUtil;

    // Mock for HttpServletRequest when testing getJwtFromHeader
    @Mock
    private HttpServletRequest request;

    // Constants for testing
    // A strong, base64-encoded secret key (should be at least 256 bits for HS256)
    private static final String TEST_SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    private static final long TEST_JWT_EXPIRATION_MS = TimeUnit.HOURS.toMillis(1); // 1 hour expiration

    private UserDetails testUserDetails;

    /**
     * Set up common test data and inject mocked @Value properties using reflection
     * before each test method runs.
     *
     * @throws NoSuchFieldException   If the specified field is not found.
     * @throws IllegalAccessException If access to the field is denied.
     */
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Use reflection to set the @Value fields for unit testing
        // In a full Spring Boot test, these would be injected automatically.
        Field secretKeyField = JwtUtil.class.getDeclaredField("SECRET_KEY");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtUtil, TEST_SECRET_KEY);

        Field expirationField = JwtUtil.class.getDeclaredField("JWT_EXPIRATION");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, TEST_JWT_EXPIRATION_MS);

        // Initialize a mock UserDetails object for testing
        testUserDetails = new User("testuser", "encodedpassword", Collections.emptyList());
    }

    /**
     * Helper method to get the signing key used by JwtUtil.
     * This method is package-private in JwtUtil, so we call it indirectly or
     * create a temporary version for test verification if needed.
     * For verification, we can simply rely on the generated token being parseable.
     */
    private SecretKey getTestSignKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(TEST_SECRET_KEY));
    }

    /**
     * Test case for `generateToken` method.
     * Verifies that a valid JWT token is generated with the correct subject,
     * issued at time, and expiration.
     */
    @Test
    void generateToken_ValidUserDetails_ReturnsSignedToken() {
        // When: Generate a token for the test user
        String token = jwtUtil.generateToken(testUserDetails);

        // Then:
        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");

        // Verify the token by parsing it with the same key
        Claims claims = Jwts.parser()
                .verifyWith(getTestSignKey()) // Use the same key for verification
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(testUserDetails.getUsername(), claims.getSubject(), "Token subject should match username");
        assertNotNull(claims.getIssuedAt(), "Issued at date should be present");
        assertNotNull(claims.getExpiration(), "Expiration date should be present");

        // Check if expiration is roughly in the future (within a small margin for execution time)
        long expectedExpirationTimeMillis = System.currentTimeMillis() + TEST_JWT_EXPIRATION_MS;
        assertTrue(claims.getExpiration().getTime() >= System.currentTimeMillis(), "Token should not be expired immediately after creation");
        assertTrue(claims.getExpiration().getTime() <= expectedExpirationTimeMillis + 1000, "Expiration should be approximately " + TEST_JWT_EXPIRATION_MS + "ms from now");
    }

    /**
     * Test case for `getJwtFromHeader` method.
     * Verifies correct extraction of token from Authorization header.
     */
    @Test
    void getJwtFromHeader_BearerTokenPresent_ExtractsToken() {
        // Given: Request header with "Bearer " prefix and a token
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.xyz.123");

        // When: Extract token
        String extractedToken = jwtUtil.getJwtFromHeader(request);

        // Then: Token should be extracted correctly
        assertEquals("abc.xyz.123", extractedToken, "Token should be extracted without 'Bearer ' prefix");
    }


    @Test
    void getJwtFromHeader_NoAuthorizationHeader_ReturnsNull() {
        // Given: No Authorization header
        when(request.getHeader("Authorization")).thenReturn(null);

        // When: Extract token
        String extractedToken = jwtUtil.getJwtFromHeader(request);

        // Then: Should return null
        assertNull(extractedToken, "Should return null if Authorization header is missing");
    }

    /**
     * Test case for `getJwtFromHeader` when Authorization header does not start with "Bearer ".
     */
    @Test
    void getJwtFromHeader_NoBearerPrefix_ReturnsNull() {
        // Given: Authorization header without "Bearer " prefix
        when(request.getHeader("Authorization")).thenReturn("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");

        // When: Extract token
        String extractedToken = jwtUtil.getJwtFromHeader(request);

        // Then: Should return null
        assertNull(extractedToken, "Should return null if 'Bearer ' prefix is missing");
    }

    /**
     * Test case for `getUserNameFromToken` method.
     * Verifies that the username (subject) is correctly extracted from a valid token.
     */
    @Test
    void getUserNameFromToken_ValidToken_ExtractsUsername() {
        // Given: A valid token generated for "testuser"
        String token = jwtUtil.generateToken(testUserDetails);

        // When: Extract username
        String username = jwtUtil.getUserNameFromToken(token);

        // Then: Username should match
        assertEquals(testUserDetails.getUsername(), username, "Extracted username should match original username");
    }

    /**
     * Test case for `getUserNameFromToken` when the token has an invalid signature.
     * Expected: `SignatureException` should be thrown.
     */
    @Test
    void getUserNameFromToken_InvalidSignature_ThrowsSignatureException() {
        // Given: A token generated with a different secret key
        SecretKey wrongKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String tokenWithWrongSignature = Jwts.builder()
                .subject("testuser")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(wrongKey) // Signed with a different key
                .compact();

        // When / Then: Assert that SignatureException is thrown
        assertThrows(SignatureException.class, () -> {
            jwtUtil.getUserNameFromToken(tokenWithWrongSignature);
        }, "SignatureException should be thrown for invalid token signature");
    }

    /**
     * Test case for `getUserNameFromToken` when the token is malformed.
     * Expected: `MalformedJwtException` should be thrown.
     */
    @Test
    void getUserNameFromToken_MalformedToken_ThrowsMalformedJwtException() {
        // Given: A malformed token string
        String malformedToken = "not.a.valid.jwt";

        // When / Then: Assert that MalformedJwtException is thrown
        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> {
            jwtUtil.getUserNameFromToken(malformedToken);
        }, "MalformedJwtException should be thrown for a malformed token");
    }

    /**
     * Test case for `getExpirationDateFromToken` method.
     * Verifies that the expiration date is correctly extracted.
     */
    @Test
    void getExpirationDateFromToken_ValidToken_ExtractsExpirationDate() {
        // Given: A valid token
        String token = jwtUtil.generateToken(testUserDetails);

        // When: Extract expiration date
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Then: Expiration date should not be null and should be in the future (relative to creation)
        assertNotNull(expirationDate, "Expiration date should not be null");
        // Verify it's roughly 'now + expiration_ms'
        long expectedExpirationMillis = System.currentTimeMillis() + TEST_JWT_EXPIRATION_MS;
        assertTrue(expirationDate.getTime() > System.currentTimeMillis(), "Expiration date should be in the future");
        // Allow a small margin for test execution time
        assertTrue(expirationDate.getTime() <= expectedExpirationMillis + 1000, "Expiration date should be approximately " + TEST_JWT_EXPIRATION_MS + "ms from now");
    }


    /**
     * Test case for `isTokenExpirated` method with a non-expired token.
     */
    @Test
    void isTokenExpirated_NonExpiredToken_ReturnsFalse() {
        // Given: Create a token that expires in the future
        String nonExpiredToken = jwtUtil.generateToken(testUserDetails); // Uses the default future expiration

        // When: Check if token is expired
        Boolean isExpired = jwtUtil.isTokenExpirated(nonExpiredToken);

        // Then: Should return false
        assertFalse(isExpired, "Token should not be considered expired");
    }


    @Test
    void validateToken_ValidTokenAndMatchingUser_ReturnsTrue() {
        // Given: A valid, non-expired token
        String token = jwtUtil.generateToken(testUserDetails);

        // When: Validate the token
        Boolean isValid = jwtUtil.validateToken(token, testUserDetails);

        // Then: Should be valid
        assertTrue(isValid, "Token should be valid for matching user and non-expired status");
    }


    @Test
    void validateToken_MismatchedUsername_ReturnsFalse() {
        // Given: A token for "anotheruser"
        UserDetails anotherUser = new User("anotheruser", "password", Collections.emptyList());
        String tokenForAnotherUser = jwtUtil.generateToken(anotherUser);

        // When: Validate this token against "testuser"
        Boolean isValid = jwtUtil.validateToken(tokenForAnotherUser, testUserDetails);

        // Then: Should be invalid due to username mismatch
        assertFalse(isValid, "Token should be invalid for mismatched username");
    }

    /**
     * Test case for `validateToken` method when the token has an invalid signature.
     * The `getUserNameFromToken` (called internally) will throw `SignatureException`.
     * The `validateToken` method itself does not catch this, so the exception is propagated.
     */
    @Test
    void validateToken_InvalidSignature_ThrowsSignatureException() {
        // Given: A token generated with a different secret key
        SecretKey wrongKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String tokenWithWrongSignature = Jwts.builder()
                .subject(testUserDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(wrongKey)
                .compact();

        // When / Then: Assert that SignatureException is thrown when validateToken is called
        assertThrows(SignatureException.class, () -> {
            jwtUtil.validateToken(tokenWithWrongSignature, testUserDetails);
        }, "SignatureException should be thrown for invalid token signature during validation");
    }
}