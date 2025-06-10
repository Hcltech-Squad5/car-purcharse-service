package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.dto.service.SellerDtoService;
import com.hcltech.car_purcharse_service.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SellerController.class)
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellerDtoService sellerDtoService;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON

    private Seller seller;
    private ResponseStructure<Seller> singleSellerFoundResponse; // Renamed for clarity
    private ResponseStructure<Seller> singleSellerCreatedResponse; // For save operations
    private ResponseStructure<List<Seller>> listSellerResponse;
    private ResponseStructure<Boolean> booleanResponse;

    @BeforeEach
    void setUp() {
        seller = new Seller(1, "Test Seller", 9876543210L, "test@example.com", "Test Company", null);

        // Response for find by ID
        singleSellerFoundResponse = new ResponseStructure<>();
        singleSellerFoundResponse.setData(seller);
        singleSellerFoundResponse.setMessage("Seller id is present"); // Corrected message
        singleSellerFoundResponse.setStatusCode(HttpStatus.FOUND.value());

        // Response for save operations (CREATE)
        singleSellerCreatedResponse = new ResponseStructure<>();
        singleSellerCreatedResponse.setData(seller);
        singleSellerCreatedResponse.setMessage("Seller details added");
        singleSellerCreatedResponse.setStatusCode(HttpStatus.CREATED.value());


        List<Seller> sellers = Arrays.asList(seller, new Seller(2, "Another Seller", 1234567890L, "another@example.com", "Another Company", null));
        listSellerResponse = new ResponseStructure<>();
        listSellerResponse.setData(sellers);
        listSellerResponse.setMessage("All Seller Found");
        listSellerResponse.setStatusCode(HttpStatus.OK.value());

        booleanResponse = new ResponseStructure<>();
        booleanResponse.setData(true);
        booleanResponse.setMessage("Seller details deleted successfully");
        booleanResponse.setStatusCode(HttpStatus.OK.value());
    }

    @Test
    void testSaveSeller() throws Exception {
        when(sellerDtoService.saveSeller(any(Seller.class)))
                .thenReturn(new ResponseEntity<>(singleSellerCreatedResponse, HttpStatus.CREATED));

        mockMvc.perform(post("/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seller)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Seller"))
                .andExpect(jsonPath("$.message").value("Seller details added"));
    }

    @Test
    void testFindSellerById() throws Exception {
        when(sellerDtoService.findSellerById(anyInt()))
                .thenReturn(new ResponseEntity<>(singleSellerFoundResponse, HttpStatus.FOUND)); // Use the correct response

        mockMvc.perform(get("/seller/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Seller id is present"));
    }

    @Test
    void testFindAllSeller() throws Exception {
        when(sellerDtoService.findAllSeller())
                .thenReturn(new ResponseEntity<>(listSellerResponse, HttpStatus.OK));

        mockMvc.perform(get("/seller")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Test Seller"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.message").value("All Seller Found"));
    }

    @Test
    void testUpdateSeller() throws Exception {
        Seller updatedSeller = new Seller(1, "Updated Seller", 9876543210L, "updated@example.com", "Updated Company", null);
        ResponseStructure<Seller> updatedResponse = new ResponseStructure<>();
        updatedResponse.setData(updatedSeller);
        updatedResponse.setMessage("Seller Updated successfully");
        updatedResponse.setStatusCode(HttpStatus.ACCEPTED.value());

        when(sellerDtoService.updateSeller(any(Seller.class), anyInt()))
                .thenReturn(new ResponseEntity<>(updatedResponse, HttpStatus.ACCEPTED));

        mockMvc.perform(put("/seller/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSeller)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.name").value("Updated Seller"))
                .andExpect(jsonPath("$.message").value("Seller Updated successfully"));
    }

    @Test
    void testDeleteSeller() throws Exception {
        when(sellerDtoService.deleteSeller(anyInt()))
                .thenReturn(new ResponseEntity<>(booleanResponse, HttpStatus.OK));

        mockMvc.perform(delete("/seller/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Seller details deleted successfully"));
    }
}