package com.hcltech.car_purcharse_service.jwt;

import com.hcltech.car_purcharse_service.dto.AuthenticationRequestDto;
import com.hcltech.car_purcharse_service.dto.AuthenticationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class AuthenticationServiceTest {

    // Mocks for the dependencies of AuthenticationService
    @Mock
    private MyUserDetailsService myUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    // The class under test, with mocked dependencies injected
    @InjectMocks
    private AuthenticationService authenticationService;

    // Common test data
    private AuthenticationRequestDto validRequestDto;
    private UserDetails userDetails;
    private Authentication authenticatedAuth;

    @BeforeEach
    void setUp() {
        // A valid authentication request DTO, NOW INCLUDING THE 'roles' FIELD
        validRequestDto = new AuthenticationRequestDto("testuser", "password", "USER");

        // Mock UserDetails object representing a successfully loaded user
        userDetails = new User("testuser", "password", new ArrayList<>()); // User (Spring Security class) for mocking UserDetails

        // Mock an authenticated Authentication object returned by AuthenticationManager
        authenticatedAuth = mock(Authentication.class);
        when(authenticatedAuth.isAuthenticated()).thenReturn(true); // Simulate successful authentication
    }

    @Test
    void login_SuccessfulAuthentication_ReturnsJwt() {
        // Given: Define the behavior of mocked dependencies for a successful scenario
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticatedAuth); // AuthenticationManager returns an authenticated object

        when(myUserDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails); // UserDetailsService loads the user details

        when(jwtUtil.generateToken(userDetails))
                .thenReturn("mocked_jwt_token"); // JwtUtil generates a token

        // When: Call the method under test
        AuthenticationResponseDto response = authenticationService.login(validRequestDto);

        // Then: Assert the expected outcomes
        assertNotNull(response, "Response should not be null");
        assertEquals("mocked_jwt_token", response.getJwt(), "The returned JWT should match the mocked one");

        // Verify that the mocked methods were called with the correct arguments
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("testuser", "password"));
        verify(myUserDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).generateToken(userDetails);
    }


    @Test
    void login_AuthenticationManagerAuthenticatesButUserDetailsServiceFails_ThrowsUsernameNotFoundException() {
        // Given: AuthenticationManager returns an authenticated object
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticatedAuth);

        // And: MyUserDetailsService throws UsernameNotFoundException when loading user details
        when(myUserDetailsService.loadUserByUsername("testuser"))
                .thenThrow(new UsernameNotFoundException("testuser not found"));

        // When / Then: Assert that UsernameNotFoundException is thrown
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.login(validRequestDto);
        }, "UsernameNotFoundException should be thrown when UserDetailsService fails after authentication");

        assertEquals("testuser not found", thrown.getMessage(), "Exception message should match");

        // Verify calls
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("testuser", "password"));
        verify(myUserDetailsService).loadUserByUsername("testuser");
        verifyNoInteractions(jwtUtil); // JWT should not be generated as UserDetails couldn't be loaded
    }


}