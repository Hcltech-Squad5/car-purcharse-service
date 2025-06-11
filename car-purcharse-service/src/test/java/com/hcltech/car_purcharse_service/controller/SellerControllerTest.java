package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.service.SellerService;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService; // Import MyUserDetailsService
import com.hcltech.car_purcharse_service.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
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
@AutoConfigureMockMvc
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellerService sellerService;

    // ADD THIS LINE: Mock MyUserDetailsService as it's a dependency for JwtFilter
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Seller seller;
    private ResponseStructure<Seller> singleSellerFoundResponse;
    private ResponseStructure<Seller> singleSellerCreatedResponse;
    private ResponseStructure<List<Seller>> listSellerResponse;
    private ResponseStructure<Boolean> booleanResponse;

    @BeforeEach
    void setUp() {
        seller = new Seller(1, "Test Seller", 9876543210L, "test@example.com", "Test Company", null);

        singleSellerFoundResponse = new ResponseStructure<>();
        singleSellerFoundResponse.setData(seller);
        singleSellerFoundResponse.setMessage("Seller id is present");
        singleSellerFoundResponse.setStatusCode(HttpStatus.OK.value());

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
        when(sellerService.saveSeller(any(Seller.class)))
                .thenReturn(new ResponseEntity<>(singleSellerCreatedResponse, HttpStatus.CREATED));

        mockMvc.perform(post("/v1/api/seller/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seller)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Seller"))
                .andExpect(jsonPath("$.message").value("Seller details added"));
    }

    @Test
    void testFindSellerById() throws Exception {
        when(sellerService.findSellerById(anyInt()))
                .thenReturn(new ResponseEntity<>(singleSellerFoundResponse, HttpStatus.FOUND));
        int id = 1;
        mockMvc.perform(get("/v1/api/seller/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Seller id is present"));
    }

    @Test
    void testFindAllSeller() throws Exception {
        when(sellerService.findAllSeller())
                .thenReturn(new ResponseEntity<>(listSellerResponse, HttpStatus.OK));

        mockMvc.perform(get("/v1/api/seller")
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
        int id = 1;

        when(sellerService.updateSeller(any(Seller.class), anyInt()))
                .thenReturn(new ResponseEntity<>(updatedResponse, HttpStatus.ACCEPTED));

        mockMvc.perform(put("/v1/api/seller/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSeller)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.name").value("Updated Seller"))
                .andExpect(jsonPath("$.message").value("Seller Updated successfully"));
    }

    @Test
    void testDeleteSeller() throws Exception {
        when(sellerService.deleteSeller(anyInt()))
                .thenReturn(new ResponseEntity<>(booleanResponse, HttpStatus.OK));
        int id = 1;

        mockMvc.perform(delete("/v1/api/seller/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("Seller details deleted successfully"));
    }
}
