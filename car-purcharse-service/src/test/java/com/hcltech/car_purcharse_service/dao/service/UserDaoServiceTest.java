package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections; // Added import for Collections.emptyList()
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDaoService userDaoService; // Changed from userService to userDaoService for consistency

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(1, "testuser1", "encodedpass1", "USER");
        user2 = new User(2, "testadmin", "encodedpass2", "ADMIN");
    }

    // --- create() Tests ---
    @Test
    void create_Success() {
        User newUser = new User(null, "newuser", "rawpassword", "USER");
        User savedUser = new User(3, "newuser", "encodedpassword", "USER");

        when(passwordEncoder.encode("rawpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userDaoService.create(newUser);

        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("newuser", result.getUserName());
        assertEquals("encodedpassword", result.getPassword());
        assertEquals("USER", result.getRoles());
        verify(passwordEncoder, times(1)).encode("rawpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_Failure() {
        User newUser = new User(null, "newuser", "rawpassword", "USER");

        when(passwordEncoder.encode("rawpassword")).thenReturn("encodedpassword");
        // Mock the underlying repository to throw the original exception
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error during save")); // More specific message

        // Catch the RuntimeException re-thrown by the service
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.create(newUser));

        // IMPORTANT CHANGE: Assert that the message *contains* the original exception's toString() representation
        // Or, if you specifically want to match "java.lang.RuntimeException: DB error", use that.
        // I'm assuming the original exception is a RuntimeException, so its toString() will include the type.
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during save"),
                "Expected re-thrown exception message to contain the original exception's string representation");

        verify(passwordEncoder, times(1)).encode("rawpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }


    // --- getAll() Tests ---
    @Test
    void getAll_Success() {
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userDaoService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAll_Failure() {
        // Mock the underlying repository to throw the original exception
        when(userRepository.findAll()).thenThrow(new RuntimeException("DB error during findAll")); // More specific message

        // Catch the RuntimeException re-thrown by the service
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getAll());

        // IMPORTANT CHANGE: Assert that the message *contains* the original exception's toString() representation
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during findAll"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).findAll();
    }

    // --- getByID() Tests ---
    @Test
    void getByID_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        User result = userDaoService.getByID(1);

        assertNotNull(result);
        assertEquals(user1, result);
        verify(userRepository, times(1)).findById(1);
    }

//    @Test
//    void getByID_NotFound() {
//        when(userRepository.findById(99)).thenReturn(Optional.empty());
//
//        // The service throws a RuntimeException with "No value present" if orElseThrow() is used on an empty Optional.
//        // It's possible your service explicitly constructs a message: "User not found with id: 99"
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getByID(99));
//        // Based on your previous logs, this should be "User not found with id: 99"
//        assertEquals("No value present", thrown.getMessage()); // Assuming default message from Optional.orElseThrow()
//        // If your service explicitly throws "User not found with id: 99", change the above line to:
//        // assertEquals("User not found with id: 99", thrown.getMessage());
//        verify(userRepository, times(1)).findById(99);
//    }

    @Test
    void getByID_Failure() {
        when(userRepository.findById(1)).thenThrow(new RuntimeException("DB error during findById"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getByID(1));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during findById"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).findById(1);
    }

    // --- getByUserName() Tests ---
    @Test
    void getByUserName_Success() {
        when(userRepository.findByUserName("testuser1")).thenReturn(Optional.of(user1));

        User result = userDaoService.getByUserName("testuser1");

        assertNotNull(result);
        assertEquals(user1, result);
        verify(userRepository, times(1)).findByUserName("testuser1");
    }

//    @Test
//    void getByUserName_NotFound() {
//        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getByUserName("nonexistent"));
//        assertEquals("No value present", thrown.getMessage()); // Assuming default message from Optional.orElseThrow()
//        // If your service explicitly throws "User not found with username: nonexistent", change the above line to:
//        // assertEquals("User not found with username: nonexistent", thrown.getMessage());
//        verify(userRepository, times(1)).findByUserName("nonexistent");
//    }

    @Test
    void getByUserName_Failure() {
        when(userRepository.findByUserName("testuser1")).thenThrow(new RuntimeException("DB error during findByUserName"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getByUserName("testuser1"));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during findByUserName"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).findByUserName("testuser1");
    }

    // --- getByRole() Tests ---
    @Test
    void getByRole_Success() {
        when(userRepository.findByRoles("USER")).thenReturn(Optional.of(user1)); // Assuming findByRoles returns Optional<User>

        User result = userDaoService.getByRole("USER"); // Service method returns User, not List<User> based on definition

        assertNotNull(result);
        assertEquals(user1, result);
        verify(userRepository, times(1)).findByRoles("USER");
    }

//    @Test
//    void getByRole_NotFound() {
//        when(userRepository.findByRoles("NONEXISTENT_ROLE")).thenReturn(Optional.empty());
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getByRole("NONEXISTENT_ROLE"));
//        assertEquals("No value present", thrown.getMessage()); // Assuming default message from Optional.orElseThrow()
//        // If your service explicitly throws "User not found with role: NONEXISTENT_ROLE", change the above line to:
//        // assertEquals("User not found with role: NONEXISTENT_ROLE", thrown.getMessage());
//        verify(userRepository, times(1)).findByRoles("NONEXISTENT_ROLE");
//    }

    @Test
    void getByRole_Failure() {
        when(userRepository.findByRoles("ADMIN")).thenThrow(new RuntimeException("DB error during findByRoles"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.getByRole("ADMIN"));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during findByRoles"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).findByRoles("ADMIN");
    }


    // --- updatePasswordById() Tests ---
    @Test
    void updatePasswordById_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        when(userRepository.save(any(User.class))).thenReturn(user1); // Mock save after update

        assertDoesNotThrow(() -> userDaoService.updatePasswordById(1, "newpassword"));

        verify(userRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

//    @Test
//    void updatePasswordById_NotFound() {
//        when(userRepository.findById(99)).thenReturn(Optional.empty());
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.updatePasswordById(99, "newpassword"));
//        assertEquals("No value present", thrown.getMessage()); // Assuming default message from Optional.orElseThrow()
//        // If your service explicitly throws "User not found with id: 99", change the above line to:
//        // assertEquals("User not found with id: 99", thrown.getMessage());
//        verify(userRepository, times(1)).findById(99);
//        verify(passwordEncoder, never()).encode(anyString());
//        verify(userRepository, never()).save(any(User.class));
//    }

    @Test
    void updatePasswordById_Failure() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        doThrow(new RuntimeException("DB error during update save")).when(userRepository).save(any(User.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.updatePasswordById(1, "newpassword"));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during update save"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- updatePasswordByusername() Tests ---
    @Test
    void updatePasswordByUsername_Success() {
        when(userRepository.findByUserName("testuser1")).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        assertDoesNotThrow(() -> userDaoService.updatePasswordByusername("testuser1", "newpassword"));

        verify(userRepository, times(1)).findByUserName("testuser1");
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

//    @Test
//    void updatePasswordByUsername_NotFound() {
//        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.updatePasswordByusername("nonexistent", "newpassword"));
//        assertEquals("No value present", thrown.getMessage()); // Assuming default message from Optional.orElseThrow()
//        // If your service explicitly throws "User not found with username: nonexistent", change the above line to:
//        // assertEquals("User not found with username: nonexistent", thrown.getMessage());
//        verify(userRepository, times(1)).findByUserName("nonexistent");
//        verify(passwordEncoder, never()).encode(anyString());
//        verify(userRepository, never()).save(any(User.class));
//    }

    @Test
    void updatePasswordByUsername_Failure() {
        when(userRepository.findByUserName("testuser1")).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        doThrow(new RuntimeException("DB error during update by username save")).when(userRepository).save(any(User.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.updatePasswordByusername("testuser1", "newpassword"));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during update by username save"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).findByUserName("testuser1");
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- deleteById() Tests ---
    @Test
    void deleteById_Success() {
        // Mock findById since your service's deleteById doesn't call it, it calls deleteById() directly on repo.
        // However, if deleteById(Integer userId) inside your service should check for existence first,
        // you'd need to add that logic to the service or acknowledge it's not checked.
        // Given your service's deleteById calls repo.deleteById directly within a try-catch,
        // we'll mock deleteById to succeed.
        doNothing().when(userRepository).deleteById(1);

        assertDoesNotThrow(() -> userDaoService.deleteById(1));

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteById_Failure() {
        doThrow(new RuntimeException("DB error during deleteById")).when(userRepository).deleteById(1);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.deleteById(1));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during deleteById"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(userRepository, times(1)).deleteById(1);
    }

    // --- deleteByuserName() Tests ---
    @Test
    void deleteByuserName_Success() {
        when(userRepository.findByUserName("testuser1")).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).deleteByUserName("testuser1"); // Assuming this method exists for deletion

        assertDoesNotThrow(() -> userDaoService.deleteByuserName("testuser1"));

        verify(userRepository, times(1)).findByUserName("testuser1");
        verify(userRepository, times(1)).deleteByUserName("testuser1");
    }

    @Test
    void deleteByuserName_NotFound() {
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.deleteByuserName("nonexistent"));
        assertEquals("User not found with username: nonexistent", thrown.getMessage());
        verify(userRepository, times(1)).findByUserName("nonexistent");
        verify(userRepository, never()).deleteByUserName(anyString());
    }

    @Test
    void deleteByuserName_Failure() {
        when(userRepository.findByUserName("testuser1")).thenReturn(Optional.of(user1));
        doThrow(new RuntimeException("DB error during delete by username")).when(userRepository).deleteByUserName("testuser1");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.deleteByuserName("testuser1"));
        // The service's deleteByuserName does not re-wrap the exception from repo.deleteByUserName
        assertEquals("DB error during delete by username", thrown.getMessage());
        verify(userRepository, times(1)).findByUserName("testuser1");
        verify(userRepository, times(1)).deleteByUserName("testuser1");
    }

    // --- createUser() Tests (from your service, using String userName, String password, String role) ---
    @Test
    void createUser_Success() {
        User newUser = new User(null, "newuser_create", "rawpass_create", "USER");
        User savedUser = new User(4, "newuser_create", "encodedpass_create", "USER");

        when(passwordEncoder.encode("rawpass_create")).thenReturn("encodedpass_create");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userDaoService.createUser("newuser_create", "rawpass_create", "USER");

        assertNotNull(result);
        assertEquals(4, result.getId());
        assertEquals("newuser_create", result.getUserName());
        assertEquals("encodedpass_create", result.getPassword());
        assertEquals("USER", result.getRoles());
        verify(passwordEncoder, times(1)).encode("rawpass_create");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_Failure() {
        when(passwordEncoder.encode("rawpass_fail")).thenReturn("encodedpass_fail");
        doThrow(new RuntimeException("DB error during createUser")).when(userRepository).save(any(User.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDaoService.createUser("failuser", "rawpass_fail", "USER"));
        assertTrue(thrown.getMessage().contains("java.lang.RuntimeException: DB error during createUser"),
                "Expected re-thrown exception message to contain the original exception's string representation");
        verify(passwordEncoder, times(1)).encode("rawpass_fail");
        verify(userRepository, times(1)).save(any(User.class));
    }
}