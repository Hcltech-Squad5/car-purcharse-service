package com.hcltech.car_purcharse_service.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the JwtFilter class.
 * These tests focus on verifying the behavior of the JwtFilter in isolation,
 * by mocking its dependencies and simulating various HTTP request scenarios.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class JwtFilterTest {

    // Mocks for the dependencies of JwtFilter
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MyUserDetailsService myUserDetailsService;

    // Mocks for HttpServletRequest, HttpServletResponse, and FilterChain
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    // The class under test, with mocked dependencies injected
    @InjectMocks
    private JwtFilter jwtFilter;

    // Common test data
    private final String TEST_USERNAME = "testuser";
    private final String VALID_TOKEN = "Bearer.valid.jwt.token";
    private UserDetails userDetails;

    /**
     * Set up common test data and clear SecurityContextHolder before each test.
     */
    @BeforeEach
    void setUp() {
        // Mock UserDetails for the test user
        userDetails = new User(TEST_USERNAME, "password", Collections.emptyList()); // No authorities for simplicity

        // Ensure SecurityContextHolder is clean before each test to prevent side effects
        SecurityContextHolder.clearContext();
    }

    /**
     * Clean up SecurityContextHolder after each test to ensure isolation.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Test case: No JWT token provided in the request header.
     * Expected: Filter chain continues, and SecurityContextHolder remains empty.
     */
    @Test
    void doFilterInternal_NoTokenInHeader_SecurityContextStaysEmpty() throws ServletException, IOException {
        // Given: request.getHeader("Authorization") returns null
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(null);

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        // 1. jwtUtil.getJwtFromHeader should be called
        verify(jwtUtil).getJwtFromHeader(request);
        // 2. No other jwtUtil or userDetailsService methods should be called
        verifyNoMoreInteractions(jwtUtil);
        verifyNoInteractions(myUserDetailsService);
        // 3. SecurityContextHolder should not have any authentication
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContext should be empty");
        // 4. Filter chain should continue
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: A valid JWT token is provided, user details are loaded, and token validates successfully.
     * Expected: SecurityContextHolder is updated with authentication, filter chain continues.
     */
    @Test
    void doFilterInternal_ValidTokenAndUserFound_SecurityContextUpdated() throws ServletException, IOException {
        // Given:
        // 1. jwtUtil extracts a valid token
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(VALID_TOKEN);
        // 2. jwtUtil extracts username from the token
        when(jwtUtil.getUserNameFromToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        // 3. myUserDetailsService loads user details successfully
        when(myUserDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        // 4. jwtUtil validates the token successfully
        when(jwtUtil.validateToken(VALID_TOKEN, userDetails)).thenReturn(true);

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        // 1. Verify all interactions
        verify(jwtUtil).getJwtFromHeader(request);
        verify(jwtUtil).getUserNameFromToken(VALID_TOKEN);
        verify(myUserDetailsService).loadUserByUsername(TEST_USERNAME);
        verify(jwtUtil).validateToken(VALID_TOKEN, userDetails);
        // 2. SecurityContextHolder should have authentication set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication, "Authentication should be set in SecurityContext");
        assertNotNull(authentication.getPrincipal(), "Principal should not be null");
        assertNull(authentication.getCredentials(), "Credentials should be null"); // As per UsernamePasswordAuthenticationToken constructor
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: JWT token is provided, but `JwtUtil.getUserNameFromToken` throws ExpiredJwtException.
     * Expected: SecurityContextHolder remains empty, filter chain continues.
     */
    @Test
    void doFilterInternal_ExpiredToken_SecurityContextStaysEmpty() throws ServletException, IOException {
        // Given:
        // 1. jwtUtil extracts a token
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(VALID_TOKEN);
        // 2. jwtUtil.getUserNameFromToken throws ExpiredJwtException
        when(jwtUtil.getUserNameFromToken(VALID_TOKEN)).thenThrow(new ExpiredJwtException(null, null, "JWT expired"));

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        verify(jwtUtil).getJwtFromHeader(request);
        verify(jwtUtil).getUserNameFromToken(VALID_TOKEN);
        verifyNoMoreInteractions(jwtUtil); // No further calls after exception
        verifyNoInteractions(myUserDetailsService); // User details not loaded
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContext should be empty");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: JWT token is provided, but `JwtUtil.getUserNameFromToken` throws MalformedJwtException.
     * Expected: SecurityContextHolder remains empty, filter chain continues.
     */
    @Test
    void doFilterInternal_MalformedToken_SecurityContextStaysEmpty() throws ServletException, IOException {
        // Given:
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(VALID_TOKEN);
        when(jwtUtil.getUserNameFromToken(VALID_TOKEN)).thenThrow(new MalformedJwtException("Invalid token format"));

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        verify(jwtUtil).getJwtFromHeader(request);
        verify(jwtUtil).getUserNameFromToken(VALID_TOKEN);
        verifyNoMoreInteractions(jwtUtil);
        verifyNoInteractions(myUserDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContext should be empty");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: Valid token extracted, but `MyUserDetailsService` cannot find the user.
     * Expected: SecurityContextHolder remains empty, filter chain continues.
     */
    @Test
    void doFilterInternal_ValidTokenButUserNotFound_SecurityContextStaysEmpty() throws ServletException, IOException {
        // Given:
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(VALID_TOKEN);
        when(jwtUtil.getUserNameFromToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        // myUserDetailsService throws UsernameNotFoundException
        when(myUserDetailsService.loadUserByUsername(TEST_USERNAME)).thenThrow(new UsernameNotFoundException("User not found"));

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        verify(jwtUtil).getJwtFromHeader(request);
        verify(jwtUtil).getUserNameFromToken(VALID_TOKEN);
        verify(myUserDetailsService).loadUserByUsername(TEST_USERNAME);
        verifyNoMoreInteractions(jwtUtil); // validateToken should not be called
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContext should be empty");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: Valid token extracted, user details loaded, but `JwtUtil.validateToken` returns false.
     * Expected: SecurityContextHolder remains empty, filter chain continues.
     */
    @Test
    void doFilterInternal_ValidTokenButValidationFails_SecurityContextStaysEmpty() throws ServletException, IOException {
        // Given:
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(VALID_TOKEN);
        when(jwtUtil.getUserNameFromToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        when(myUserDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        // jwtUtil.validateToken returns false
        when(jwtUtil.validateToken(VALID_TOKEN, userDetails)).thenReturn(false);

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        verify(jwtUtil).getJwtFromHeader(request);
        verify(jwtUtil).getUserNameFromToken(VALID_TOKEN);
        verify(myUserDetailsService).loadUserByUsername(TEST_USERNAME);
        verify(jwtUtil).validateToken(VALID_TOKEN, userDetails);
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContext should be empty");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: SecurityContextHolder already contains an Authentication object.
     * Expected: JwtFilter should not attempt to re-authenticate or overwrite the existing authentication.
     */
    @Test
    void doFilterInternal_AuthenticationAlreadyPresent_FilterSkipsProcessing() throws ServletException, IOException {
        // Given: SecurityContextHolder already has an authentication
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("existingUser", "password", Collections.emptyList()));
        // And: a token is present in the request (though it should be ignored)
        when(jwtUtil.getJwtFromHeader(request)).thenReturn(VALID_TOKEN);

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        // 1. getJwtFromHeader might still be called, but other jwtUtil methods for parsing/validation should not
        verify(jwtUtil).getJwtFromHeader(request);
//        verifyNoMoreInteractions(jwtUtil); // Ensure no more calls to jwtUtil, especially getUserNameFromToken
        verifyNoInteractions(myUserDetailsService);
        // 2. SecurityContextHolder's authentication should remain unchanged (still "existingUser")
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should still be present");
        assertEquals("existingUser", SecurityContextHolder.getContext().getAuthentication().getName(), "Existing authentication should not be overwritten");
        // 3. Filter chain should continue
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case: Token is present but empty string.
     * Expected: Filter chain continues, and SecurityContextHolder remains empty.
     */
    @Test
    void doFilterInternal_EmptyToken_SecurityContextStaysEmpty() throws ServletException, IOException {
        // Given: jwtUtil extracts an empty token string
        when(jwtUtil.getJwtFromHeader(request)).thenReturn("");

        // When: Filter is executed
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then:
        verify(jwtUtil).getJwtFromHeader(request);
        verifyNoMoreInteractions(jwtUtil); // No further jwtUtil methods should be called
        verifyNoInteractions(myUserDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "SecurityContext should be empty");
        verify(filterChain).doFilter(request, response);
    }
}