package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.PurchasedCarDto;
import com.hcltech.car_purcharse_service.dto.PurchasedCarResponseDto;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService; // Import MyUserDetailsService
import com.hcltech.car_purcharse_service.dao.service.PurchasedCarDaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach; // Import BeforeEach
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // <--- USE THIS
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchasedCarController.class) // <--- Changed from @SpringBootTest
@AutoConfigureMockMvc
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
public class PurchasedCarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchasedCarDaoService purchasedCarDaoService;

    // ADD THIS LINE: Mock MyUserDetailsService as it's a dependency for JwtFilter
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PurchasedCarResponseDto sampleResponse;
    private PurchasedCarDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleResponse = new PurchasedCarResponseDto(1, 1, 2, 3, LocalDate.of(2024, 5, 20));
        sampleDto = new PurchasedCarDto(1, 2, 3, LocalDate.of(2024, 5, 20));
    }

    @Test
    void testCreatePurchasedCar() throws Exception {
        Mockito.when(purchasedCarDaoService.createPurchasedCar(any(PurchasedCarDto.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/v1/api/purchased-cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllPurchasedCars() throws Exception {
        List<PurchasedCarResponseDto> responseList = Collections.singletonList(sampleResponse);

        Mockito.when(purchasedCarDaoService.getAllPurchasedCars()).thenReturn(responseList);

        mockMvc.perform(get("/v1/api/purchased-cars/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetPurchasedCarById() throws Exception {
        Mockito.when(purchasedCarDaoService.getPurchasedCarById(1)).thenReturn(sampleResponse);
        int id =1;
        mockMvc.perform(get("/v1/api/purchased-cars/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdatePurchasedCar() throws Exception {
        Mockito.when(purchasedCarDaoService.updatePurchasedCar(eq(1), any(PurchasedCarDto.class)))
                .thenReturn(sampleResponse);
        int id =1;
        mockMvc.perform(put("/v1/api/purchased-cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeletePurchasedCar() throws Exception {
        Mockito.doNothing().when(purchasedCarDaoService).deletePurchasedCar(1);
        int id =1;
        mockMvc.perform(delete("/v1/api/purchased-cars/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPurchasedCarsByBuyerId() throws Exception {
        Mockito.when(purchasedCarDaoService.getPurchasedCarsByBuyerId(1))
                .thenReturn(Collections.singletonList(sampleResponse));
        int id =1;
        mockMvc.perform(get("/v1/api/purchased-cars/buyer/{buyerId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetPurchasedCarsBySellerId() throws Exception {
        // Assuming sampleResponse has sellerId as 2 (from your sampleDto)
        Mockito.when(purchasedCarDaoService.getPurchasedCarsBySellerId(2))
                .thenReturn(Collections.singletonList(
                        new PurchasedCarResponseDto(sampleResponse.getId(), sampleResponse.getBuyerId(), 2, sampleResponse.getCarId(), sampleResponse.getPurchaseDate())
                ));
        int id =2;
        mockMvc.perform(get("/v1/api/purchased-cars/seller/{sellerId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sellerId").value(2)); // Assert on sellerId
    }

    @Test
    void testGetPurchasedCarsByCarId() throws Exception {
        Mockito.when(purchasedCarDaoService.getPurchasedCarsByCarId(3))
                .thenReturn(Collections.singletonList(sampleResponse));

        mockMvc.perform(get("/v1/api/purchased-cars/car/{carId}",3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}