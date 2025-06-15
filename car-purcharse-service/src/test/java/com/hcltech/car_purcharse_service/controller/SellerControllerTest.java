package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.SellerDto;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService;
import com.hcltech.car_purcharse_service.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the SellerController class.
 * Uses @WebMvcTest to test the controller layer in isolation,
 * mocking the SellerService dependency.
 */
@WebMvcTest(SellerController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"BUYER"})
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests to the controller

    @MockitoBean
    private SellerService sellerService; // Mock the SellerService dependency

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    private SellerDto sellerDto; // Common SellerDto object for tests

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;
    /**
     * Set up common test data before each test method runs.
     */
    @BeforeEach
    void setUp() {
        sellerDto = new SellerDto();
        sellerDto.setId(1);
        sellerDto.setName("Test Seller");
        sellerDto.setEmail("test@example.com");
        sellerDto.setPassword("password"); // Password should ideally not be returned, but for DTO consistency
    }

    /**
     * Test case for the saveSeller endpoint.
     * Verifies that a seller can be saved successfully.
     * @throws Exception if an error occurs during mockMvc performance
     */
    @Test
    void saveSeller_ShouldReturnCreated() throws Exception {
        // Mock the service behavior: when saveSeller is called with any SellerDto, return the prepared sellerDto.
        when(sellerService.saveSeller(any(SellerDto.class))).thenReturn(sellerDto);

        // Perform a POST request to /v1/api/sellers/create
        // Set content type to application/json and provide the sellerDto as JSON body
        // Expect HTTP status 201 (Created)
        // Expect the JSON response to match the fields of sellerDto
        mockMvc.perform(post("/v1/api/sellers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sellerDto.getId()))
                .andExpect(jsonPath("$.name").value(sellerDto.getName()))
                .andExpect(jsonPath("$.email").value(sellerDto.getEmail()));
    }

    /**
     * Test case for the findSellerById endpoint.
     * Verifies that a seller can be retrieved by ID successfully.
     * @throws Exception if an error occurs during mockMvc performance
     */
    @Test
    void findSellerById_ShouldReturnOk() throws Exception {
        // Mock the service behavior: when findSellerById is called with any integer, return the prepared sellerDto.
        when(sellerService.findSellerById(anyInt())).thenReturn(sellerDto);

        // Perform a GET request to /v1/api/sellers/1
        // Expect HTTP status 200 (OK)
        // Expect the JSON response to match the fields of sellerDto
        mockMvc.perform(get("/v1/api/sellers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sellerDto.getId()))
                .andExpect(jsonPath("$.name").value(sellerDto.getName()));
    }

    /**
     * Test case for the findAllSeller endpoint.
     * Verifies that all sellers can be retrieved successfully.
     * @throws Exception if an error occurs during mockMvc performance
     */
    @Test
    void findAllSeller_ShouldReturnOk() throws Exception {
        // Create a list of SellerDto objects for the mock service response.
        List<SellerDto> sellerList = Arrays.asList(sellerDto, new SellerDto());
        sellerList.get(1).setId(2);
        sellerList.get(1).setName("Another Seller");

        // Mock the service behavior: when findAllSeller is called, return the list of sellerDtos.
        when(sellerService.findAllSeller()).thenReturn(sellerList);

        // Perform a GET request to /v1/api/sellers
        // Expect HTTP status 200 (OK)
        // Expect the JSON response to be an array of size 2
        // Expect the first element's ID to match sellerDto.getId()
        mockMvc.perform(get("/v1/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(sellerDto.getId()));
    }

    /**
     * Test case for the updateSeller endpoint.
     * Verifies that a seller can be updated successfully.
     * @throws Exception if an error occurs during mockMvc performance
     */
    @Test
    void updateSeller_ShouldReturnCreated() throws Exception {
        // Create an updated SellerDto for the test.
        SellerDto updatedSellerDto = new SellerDto();
        updatedSellerDto.setId(1);
        updatedSellerDto.setName("Updated Seller Name");
        updatedSellerDto.setEmail("updated@example.com");

        // Mock the service behavior: when updateSeller is called with any SellerDto and integer, return the updatedSellerDto.
        when(sellerService.updateSeller(any(SellerDto.class), anyInt())).thenReturn(updatedSellerDto);

        // Perform a PUT request to /v1/api/sellers/1
        // Set content type to application/json and provide the updatedSellerDto as JSON body
        // Expect HTTP status 201 (Created)
        // Expect the JSON response to match the fields of updatedSellerDto
        mockMvc.perform(put("/v1/api/sellers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSellerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(updatedSellerDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedSellerDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedSellerDto.getEmail()));
    }

    /**
     * Test case for the deleteSeller endpoint.
     * Verifies that a seller can be deleted successfully.
     * @throws Exception if an error occurs during mockMvc performance
     */
    @Test
    void deleteSeller_ShouldReturnOk() throws Exception {
        // Mock the service behavior: when deleteSeller is called with any integer, do nothing (simulate successful deletion).
        doNothing().when(sellerService).deleteSeller(anyInt());

        // Perform a DELETE request to /v1/api/sellers/1
        // Expect HTTP status 200 (OK)
        mockMvc.perform(delete("/v1/api/sellers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
