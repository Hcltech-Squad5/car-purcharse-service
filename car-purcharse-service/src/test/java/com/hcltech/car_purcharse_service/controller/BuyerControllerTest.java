package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService; // Import MyUserDetailsService
import com.hcltech.car_purcharse_service.service.BuyerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // <--- Changed this
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuyerController.class) // <-- Use WebMvcTest here
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
class BuyerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuyerService buyerService;

    // ADD THIS LINE: Mock MyUserDetailsService as it's a dependency for JwtFilter
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private BuyerDto buyerDto1;
    private BuyerDto buyerDto2;

    @BeforeEach
    void setUp() {
        buyerDto1 = new BuyerDto();
        buyerDto1.setId(1);
        buyerDto1.setFirstName("Alice");
        buyerDto1.setLastName("Smith");
        buyerDto1.setEmail("alice@example.com");
        buyerDto1.setPhoneNumber("123-456-7890");
        buyerDto1.setPassword("password123");

        buyerDto2 = new BuyerDto();
        buyerDto2.setId(2);
        buyerDto2.setFirstName("Bob");
        buyerDto2.setLastName("Johnson");
        buyerDto2.setEmail("bob@example.com");
        buyerDto2.setPhoneNumber("987-654-3210");
        buyerDto2.setPassword("securepass");
    }

    @Test
    void createBuyer_Success() throws Exception {
        BuyerDto newBuyerDto = new BuyerDto();
        newBuyerDto.setFirstName("Charlie");
        newBuyerDto.setLastName("Brown");
        newBuyerDto.setEmail("charlie@example.com");
        newBuyerDto.setPhoneNumber("555-123-4567");
        newBuyerDto.setPassword("charliePass");

        BuyerDto createdBuyerDto = new BuyerDto();
        createdBuyerDto.setId(3);
        createdBuyerDto.setFirstName("Charlie");
        createdBuyerDto.setLastName("Brown");
        createdBuyerDto.setEmail("charlie@example.com");
        createdBuyerDto.setPhoneNumber("555-123-4567");
        createdBuyerDto.setPassword("charliePass");

        when(buyerService.createBuyer(any(BuyerDto.class))).thenReturn(createdBuyerDto);

        mockMvc.perform(post("/v1/api/buyer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBuyerDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.firstName").value("Charlie"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"));

        verify(buyerService, times(1)).createBuyer(any(BuyerDto.class));
    }

    @Test
    void createBuyer_ValidationFailure_NotBlank() throws Exception {
        // Arrange: Create a BuyerDto with invalid data
        BuyerDto invalidBuyerDto = new BuyerDto();
        // firstName is null (will trigger @NotBlank)
        invalidBuyerDto.setLastName("Doe");
        invalidBuyerDto.setEmail("invalid@example.com");
        invalidBuyerDto.setPhoneNumber("123-456-7890");
        invalidBuyerDto.setPassword("short");


        mockMvc.perform(post("/v1/api/buyer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBuyerDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").value("First name is required"))
                .andExpect(jsonPath("$.password").value("Password must be at least 8 characters long"));

        verify(buyerService, times(0)).createBuyer(any(BuyerDto.class));
    }

    @Test
    void getBuyerById_Success() throws Exception {
        when(buyerService.getBuyerById(1)).thenReturn(buyerDto1);

        mockMvc.perform(get("/v1/api/buyer/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(buyerService, times(1)).getBuyerById(1);
    }

    @Test
    void getBuyerById_NotFound() throws Exception {
        when(buyerService.getBuyerById(99)).thenThrow(new RuntimeException("Buyer not found"));

        mockMvc.perform(get("/v1/api/buyer/{id}", 99)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // Or whatever status your controller returns for not found.

        verify(buyerService, times(1)).getBuyerById(99);
    }

    @Test
    void getAllBuyers_Success() throws Exception {
        List<BuyerDto> allBuyers = Arrays.asList(buyerDto1, buyerDto2);
        when(buyerService.getAllBuyers()).thenReturn(allBuyers);

        mockMvc.perform(get("/v1/api/buyer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Bob"))
                .andExpect(jsonPath("$[1].lastName").value("Johnson"));

        verify(buyerService, times(1)).getAllBuyers();
    }

    @Test
    void updateBuyer_Success() throws Exception {
        BuyerDto updatedBuyerInfo = new BuyerDto();
        updatedBuyerInfo.setFirstName("Alice");
        updatedBuyerInfo.setLastName("Wonderland");
        updatedBuyerInfo.setEmail("alice.w@example.com");
        updatedBuyerInfo.setPhoneNumber("111-222-3333");
        updatedBuyerInfo.setPassword("newpass456");

        BuyerDto returnedBuyerDto = new BuyerDto();
        returnedBuyerDto.setId(1);
        returnedBuyerDto.setFirstName("Alice");
        returnedBuyerDto.setLastName("Wonderland");
        returnedBuyerDto.setEmail("alice.w@example.com");
        returnedBuyerDto.setPhoneNumber("111-222-3333");
        returnedBuyerDto.setPassword("newpass456");

        when(buyerService.updateBuyer(eq(1), any(BuyerDto.class))).thenReturn(returnedBuyerDto);

        mockMvc.perform(put("/v1/api/buyer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBuyerInfo))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Wonderland"))
                .andExpect(jsonPath("$.email").value("alice.w@example.com"));

        verify(buyerService, times(1)).updateBuyer(eq(1), any(BuyerDto.class));
    }

    @Test
    void deleteBuyer_Success() throws Exception {
        doNothing().when(buyerService).deleteBuyer(1);

        mockMvc.perform(delete("/v1/api/buyer/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(buyerService, times(1)).deleteBuyer(1);
    }
}