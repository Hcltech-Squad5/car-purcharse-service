package com.hcltech.car_purcharse_service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException should be a RuntimeException")
    void ResourceNotFoundException_isRuntimeException() {
        // Verify that ResourceNotFoundException is a subclass of RuntimeException
        assertTrue(RuntimeException.class.isAssignableFrom(ResourceNotFoundException.class));
    }

    @Test
    @DisplayName("ResourceNotFoundException should store and return the correct message")
    void ResourceNotFoundException_storesAndReturnsMessage() {
        String testMessage = "Test resource not found message.";
        ResourceNotFoundException exception = new ResourceNotFoundException(testMessage);

        assertNotNull(exception); // Ensure the exception object is created
        assertEquals(testMessage, exception.getMessage()); // Verify the message
    }

    @Test
    @DisplayName("ResourceNotFoundException should handle null message gracefully")
    void ResourceNotFoundException_handlesNullMessage() {
        // When a Throwable (like RuntimeException) is constructed with a null message,
        // its getMessage() method often returns null.
        ResourceNotFoundException exception = new ResourceNotFoundException(null);

        assertNotNull(exception);
        assertEquals(null, exception.getMessage());
    }

    @Test
    @DisplayName("ResourceNotFoundException should handle empty message gracefully")
    void ResourceNotFoundException_handlesEmptyMessage() {
        String emptyMessage = "";
        ResourceNotFoundException exception = new ResourceNotFoundException(emptyMessage);

        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }
}