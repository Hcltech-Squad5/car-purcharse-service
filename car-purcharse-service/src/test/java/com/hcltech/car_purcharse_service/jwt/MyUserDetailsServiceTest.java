package com.hcltech.car_purcharse_service.jwt;

import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository; // Mock the UserRepository dependency

    @InjectMocks
    private MyUserDetailsService myUserDetailsService; // Inject the mock into the service under test

    private User testUser;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "testpassword";
    private final String TEST_ROLES = "USER"; // Example role

    @BeforeEach
    void setUp() {
        // Initialize a test User object before each test
        testUser = new User();
        testUser.setId(1); // Assuming User has an ID
        testUser.setUserName(TEST_USERNAME);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setRoles(TEST_ROLES);
    }

    /**
     * Test cases for loadUserByUsername(String username) method.
     */
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given: The userRepository finds the user by username
        when(userRepository.findByUserName(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // When: loadUserByUsername is called
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(TEST_USERNAME);

        // Then: Verify the returned UserDetails object's properties
        assertNotNull(userDetails);
        assertEquals(TEST_USERNAME, userDetails.getUsername());
        assertEquals(TEST_PASSWORD, userDetails.getPassword()); // In a real app, this would be encoded
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + TEST_ROLES.toUpperCase()))); // Spring Security prefixes roles with "ROLE_"
        verify(userRepository, times(1)).findByUserName(TEST_USERNAME); // Verify findByUserName was called
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        // Given: The userRepository returns an empty Optional (user not found)
        when(userRepository.findByUserName("nonexistentuser")).thenReturn(Optional.empty());

        // When & Then: Assert that UsernameNotFoundException is thrown with the correct message
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername("nonexistentuser"));

        assertEquals("The user is not found", exception.getMessage());
        verify(userRepository, times(1)).findByUserName("nonexistentuser"); // Verify findByUserName was called
    }

    @Test
    void loadUserByUsername_ShouldHandleNullUsername() {
        // Given: Calling findByUserName with null might cause an exception from the repository layer,
        // or return empty. We'll mock it to simulate a common scenario where the repository
        // itself might throw an IllegalArgumentException for null input.
        when(userRepository.findByUserName(null)).thenThrow(new IllegalArgumentException("Username cannot be null"));

        // When & Then: Assert that an IllegalArgumentException (or whatever the repository would throw) is propagated
        assertThrows(IllegalArgumentException.class,
                () -> myUserDetailsService.loadUserByUsername(null));

        verify(userRepository, times(1)).findByUserName(null); // Verify findByUserName was called with null
    }

    @Test
    void loadUserByUsername_ShouldHandleEmptyUsername() {
        // Given: The userRepository returns an empty Optional for an empty string username
        when(userRepository.findByUserName("")).thenReturn(Optional.empty());

        // When & Then: Assert that UsernameNotFoundException is thrown for an empty username
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername(""));

        assertEquals("The user is not found", exception.getMessage());
        verify(userRepository, times(1)).findByUserName("");
    }

    @Test
    void loadUserByUsername_ShouldHandleUserWithNullRoles() {
        // Given: A user exists, but their roles field is null
        testUser.setRoles(null);
        when(userRepository.findByUserName(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // When & Then: Calling loadUserByUsername should still work, but the roles part might be empty
        // The .toUpperCase() on a null string would cause a NullPointerException, which needs to be handled
        // in the actual service if roles can be null. For now, we expect NPE.
        // If roles can be null, update the service to handle it (e.g., provide a default role or filter null).

        // Current service code: .roles(user.getRoles().toUpperCase()) will throw NPE if user.getRoles() is null.
        // This test case highlights that.
        assertThrows(NullPointerException.class, () -> myUserDetailsService.loadUserByUsername(TEST_USERNAME));

        verify(userRepository, times(1)).findByUserName(TEST_USERNAME);
    }

}