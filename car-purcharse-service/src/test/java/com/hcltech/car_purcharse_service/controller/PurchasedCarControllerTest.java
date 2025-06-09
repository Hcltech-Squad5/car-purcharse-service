package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.dto.PurchasedCarDto;
import com.hcltech.car_purcharse_service.dto.PurchasedCarResponseDto;
import com.hcltech.car_purcharse_service.service.PurchasedCarService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchasedCarController.class)
public class PurchasedCarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchasedCarService purchasedCarService;

    @Autowired
    private ObjectMapper objectMapper;

    private final PurchasedCarResponseDto sampleResponse = new PurchasedCarResponseDto(1, 1, 2, 3, LocalDate.of(2024, 5, 20));
    private final PurchasedCarDto sampleDto = new PurchasedCarDto(1, 2, 3, LocalDate.of(2024, 5, 20));

    @Test
    void testCreatePurchasedCar() throws Exception {
        Mockito.when(purchasedCarService.createPurchasedCar(any(PurchasedCarDto.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/purchased-cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllPurchasedCars() throws Exception {
        List<PurchasedCarResponseDto> responseList = Collections.singletonList(sampleResponse);

        Mockito.when(purchasedCarService.getAllPurchasedCars()).thenReturn(responseList);

        mockMvc.perform(get("/api/purchased-cars/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetPurchasedCarById() throws Exception {
        Mockito.when(purchasedCarService.getPurchasedCarById(1)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/purchased-cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdatePurchasedCar() throws Exception {
        Mockito.when(purchasedCarService.updatePurchasedCar(eq(1), any(PurchasedCarDto.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/purchased-cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeletePurchasedCar() throws Exception {
        Mockito.doNothing().when(purchasedCarService).deletePurchasedCar(1);

        mockMvc.perform(delete("/api/purchased-cars/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPurchasedCarsByBuyerId() throws Exception {
        Mockito.when(purchasedCarService.getPurchasedCarsByBuyerId(1))
                .thenReturn(Collections.singletonList(sampleResponse));

        mockMvc.perform(get("/api/purchased-cars/buyer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buyerId").value(1));
    }

    @Test
    void testGetPurchasedCarsBySellerId() throws Exception {
        Mockito.when(purchasedCarService.getPurchasedCarsBySellerId(2))
                .thenReturn(Collections.singletonList(sampleResponse));

        mockMvc.perform(get("/api/purchased-cars/seller/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sellerId").value(2));
    }

    @Test
    void testGetPurchasedCarsByCarId() throws Exception {
        Mockito.when(purchasedCarService.getPurchasedCarsByCarId(3))
                .thenReturn(Collections.singletonList(sampleResponse));

        mockMvc.perform(get("/api/purchased-cars/car/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carId").value(3));
    }
}