package com.hcltech.car_purcharse_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    // Helper to create a simple WebRequest (required by some handlers)
    private WebRequest createWebRequest() {
        return new ServletWebRequest(null); // No actual HttpServletRequest needed for these tests
    }

    // Helper method to convert byte array to hex string for debugging hidden chars
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }


    // --- handleValidationExceptions (MethodArgumentNotValidException) Tests ---
    @Test
    @DisplayName("handleValidationExceptions should return BAD_REQUEST with field errors")
    void handleValidationExceptions_returnsBadRequestWithErrors() {
        // Mock BindingResult and FieldErrors
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("objectName", "fieldName1", "Error message 1");
        FieldError error2 = new FieldError("objectName", "fieldName2", "Error message 2");

        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(error1, error2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().containsKey("fieldName1"));
        assertTrue(response.getBody().containsKey("fieldName2"));
        assertEquals("Error message 1", response.getBody().get("fieldName1"));
        assertEquals("Error message 2", response.getBody().get("fieldName2"));
    }

    @Test
    @DisplayName("handleValidationExceptions should return BAD_REQUEST with empty map if no field errors")
    void handleValidationExceptions_noErrors_returnsBadRequestWithEmptyMap() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}