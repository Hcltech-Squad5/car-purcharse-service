package com.hcltech.car_purcharse_service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdNotFoundExceptionTest {

    @Test
    @DisplayName("IdNotFoundException should be a RuntimeException")
    void IdNotFoundException_isRuntimeException() {
        // Verify that IdNotFoundException is a subclass of RuntimeException
        assertTrue(RuntimeException.class.isAssignableFrom(IdNotFoundException.class));
    }

    @Test
    @DisplayName("IdNotFoundException should store and return the correct message")
    void IdNotFoundException_storesAndReturnsMessage() {
        String testMessage = "Test ID not found message.";
        IdNotFoundException exception = new IdNotFoundException(testMessage);

        assertNotNull(exception); // Ensure the exception object is created
        assertEquals(testMessage, exception.getMessage()); // Verify the message
    }

    @Test
    @DisplayName("IdNotFoundException should handle null message gracefully")
    void IdNotFoundException_handlesNullMessage() {
        IdNotFoundException exception = new IdNotFoundException(null);

        assertNotNull(exception);
        // getMessage() on an exception constructed with null returns null
        assertEquals(null, exception.getMessage());
    }

    @Test
    @DisplayName("IdNotFoundException should handle empty message gracefully")
    void IdNotFoundException_handlesEmptyMessage() {
        String emptyMessage = "";
        IdNotFoundException exception = new IdNotFoundException(emptyMessage);

        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }
}