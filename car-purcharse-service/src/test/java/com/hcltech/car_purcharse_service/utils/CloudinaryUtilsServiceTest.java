package com.hcltech.car_purcharse_service.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader; // Import Uploader explicitly
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryUtilsServiceTest {

    @Mock
    private Cloudinary cloudinary; // Mock the Cloudinary main object

    @Mock
    private Uploader uploader;     // Mock the Uploader component (returned by cloudinary.uploader())

    @InjectMocks
    private CloudinaryUtilsService cloudinaryUtilsService; // Inject mocks into the service under test

    private byte[] testImageBytes;
    private String testPublicId;

    @BeforeEach
    void setUp() {
        // Ensure that when cloudinary.uploader() is called, it returns our mocked uploader
        when(cloudinary.uploader()).thenReturn(uploader);

        testImageBytes = "test_image_content".getBytes();
        testPublicId = "my_image_public_id";
    }

    /**
     * Test cases for uploadImage(byte[] image) method.
     */
    @Test
    void uploadImage_ShouldReturnImageDetailsMap_WhenUploadIsSuccessful() throws IOException {
        // Given: A successful upload result map
        Map<String, String> successfulUploadResult = new HashMap<>();
        successfulUploadResult.put("public_id", testPublicId);
        successfulUploadResult.put("url", "http://example.com/test_image.jpg");
        successfulUploadResult.put("secure_url", "https://example.com/test_image.jpg");

        // When: uploader.upload is called with any byte array and empty map, return the successful result
        when(uploader.upload(eq(testImageBytes), eq(ObjectUtils.emptyMap()))).thenReturn(successfulUploadResult);

        // Act: Call the service method
        Map result = cloudinaryUtilsService.uploadImage(testImageBytes);

        // Then: Verify the returned map and that the upload method was called
        assertNotNull(result);
        assertEquals(testPublicId, result.get("public_id"));
        assertEquals("https://example.com/test_image.jpg", result.get("secure_url"));
        verify(uploader, times(1)).upload(eq(testImageBytes), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void uploadImage_ShouldThrowRuntimeException_WhenIOExceptionOccurs() throws IOException {
        // Given: uploader.upload throws an IOException
        IOException expectedException = new IOException("Simulated network error");
        when(uploader.upload(any(byte[].class), eq(ObjectUtils.emptyMap()))).thenThrow(expectedException);

        // Act & Then: Assert that a RuntimeException is thrown and its cause is the IOException
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> cloudinaryUtilsService.uploadImage(testImageBytes));

        assertEquals(expectedException, thrown.getCause()); // Verify the original IOException is the cause
        verify(uploader, times(1)).upload(any(byte[].class), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void uploadImage_ShouldHandleNullImageBytes_IfCloudinaryAllows() throws IOException {
        // Given: Cloudinary's uploader might handle null bytes (or throw its own NPE/IAE)
        // For this test, we assume if it hits Cloudinary it will return something or throw.
        // Let's assume Cloudinary would return a map even for null if it's not checked pre-emptively
        // Or, more realistically, it would throw an exception which will be caught by the service.
        IOException expectedException = new IOException("Input stream cannot be null");
        when(uploader.upload(eq(null), eq(ObjectUtils.emptyMap()))).thenThrow(expectedException);

        // Act & Then: Assert that a RuntimeException is thrown for null input
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> cloudinaryUtilsService.uploadImage(null));

        assertEquals(expectedException, thrown.getCause());
        verify(uploader, times(1)).upload(eq(null), eq(ObjectUtils.emptyMap()));
    }


    /**
     * Test cases for deleteImage(String publicId) method.
     */
    @Test
    void deleteImage_ShouldReturnOk_WhenDeletionIsSuccessful() throws Exception {
        // Given: A successful deletion result map
        Map<String, String> successfulDeletionResult = new HashMap<>();
        successfulDeletionResult.put("result", "ok");

        // When: uploader.destroy is called with the publicId and empty map, return the successful result
        when(uploader.destroy(eq(testPublicId), eq(ObjectUtils.emptyMap()))).thenReturn(successfulDeletionResult);

        // Act: Call the service method
        String result = cloudinaryUtilsService.deleteImage(testPublicId);

        // Then: Verify the returned string and that the destroy method was called
        assertEquals("ok", result);
        verify(uploader, times(1)).destroy(eq(testPublicId), eq(ObjectUtils.emptyMap()));
    }


    @Test
    void deleteImage_ShouldHandleNullPublicId() throws Exception {
        // Given: Cloudinary's destroy might throw an exception for null publicId
        Exception expectedException = new IllegalArgumentException("Public ID cannot be null");
        when(uploader.destroy(eq(null), eq(ObjectUtils.emptyMap()))).thenThrow(expectedException);

        // Act: Call the service method with null
        String result = cloudinaryUtilsService.deleteImage(null);

        // Then: Verify the returned error message
        assertTrue(result.startsWith("Error: "));
        assertTrue(result.contains("Public ID cannot be null"));
        verify(uploader, times(1)).destroy(eq(null), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void deleteImage_ShouldHandleEmptyPublicId() throws Exception {
        // Given: Cloudinary's destroy might throw an exception for empty publicId
        Exception expectedException = new IllegalArgumentException("Public ID cannot be empty");
        when(uploader.destroy(eq(""), eq(ObjectUtils.emptyMap()))).thenThrow(expectedException);

        // Act: Call the service method with empty string
        String result = cloudinaryUtilsService.deleteImage("");

        // Then: Verify the returned error message
        assertTrue(result.startsWith("Error: "));
        assertTrue(result.contains("Public ID cannot be empty"));
        verify(uploader, times(1)).destroy(eq(""), eq(ObjectUtils.emptyMap()));
    }
}