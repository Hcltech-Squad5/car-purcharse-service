package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.ReviewDto;
import com.hcltech.car_purcharse_service.dao.service.ReviewDaoService;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewDaoService reviewDaoService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewDto reviewDto1;
    private ReviewDto reviewDto2;

    @BeforeEach
    void setUp() {
        reviewDto1 = new ReviewDto();
        reviewDto1.setId(1);
        reviewDto1.setRate("5");
        reviewDto1.setFeedback("Excellent car, great service!");
        reviewDto1.setBuyerId(101);
        reviewDto1.setCarId(201);

        reviewDto2 = new ReviewDto();
        reviewDto2.setId(2);
        reviewDto2.setRate("4");
        reviewDto2.setFeedback("Good car, but delivery was delayed.");
        reviewDto2.setBuyerId(102);
        reviewDto2.setCarId(202);
    }

    @Test
    void createReview_Success() throws Exception {
        ReviewDto newReviewDto = new ReviewDto();
        newReviewDto.setRate("5");
        newReviewDto.setFeedback("Amazing experience!");
        newReviewDto.setBuyerId(103);
        newReviewDto.setCarId(203);

        ReviewDto createdReviewDto = new ReviewDto();
        createdReviewDto.setId(3);
        createdReviewDto.setRate("5");
        createdReviewDto.setFeedback("Amazing experience!");
        createdReviewDto.setBuyerId(103);
        createdReviewDto.setCarId(203);

        when(reviewDaoService.createReview(any(ReviewDto.class))).thenReturn(createdReviewDto);

        mockMvc.perform(post("/v1/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.rate").value("5"))
                .andExpect(jsonPath("$.feedback").value("Amazing experience!"));

        verify(reviewDaoService, times(1)).createReview(any(ReviewDto.class));
    }

    @Test
    void getAllReviews_Success() throws Exception {
        List<ReviewDto> allReviews = Arrays.asList(reviewDto1, reviewDto2);
        when(reviewDaoService.getAllReviews()).thenReturn(allReviews);

        mockMvc.perform(get("/v1/api/reviews")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rate").value("5"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].rate").value("4"));

        verify(reviewDaoService, times(1)).getAllReviews();
    }

    @Test
    void getReviewById_Success() throws Exception {
        when(reviewDaoService.getReviewById(1)).thenReturn(reviewDto1);

        mockMvc.perform(get("/v1/api/reviews/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rate").value("5"))
                .andExpect(jsonPath("$.feedback").value("Excellent car, great service!"));

        verify(reviewDaoService, times(1)).getReviewById(1);
    }

    @Test
    void getReviewById_NotFound() throws Exception {
        when(reviewDaoService.getReviewById(99)).thenThrow(new RuntimeException("Review not found with ID: 99"));

        mockMvc.perform(get("/v1/api/reviews/{id}", 99)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // Or the appropriate error status from your exception handling

        verify(reviewDaoService, times(1)).getReviewById(99);
    }

    @Test
    void updateReview_Success() throws Exception {
        ReviewDto updatedReviewInfo = new ReviewDto();
        updatedReviewInfo.setRate("4");
        updatedReviewInfo.setFeedback("Car is good, but pricing could be better.");
        updatedReviewInfo.setBuyerId(reviewDto1.getBuyerId()); // Keep original buyer/car for update logic
        updatedReviewInfo.setCarId(reviewDto1.getCarId());

        ReviewDto returnedReviewDto = new ReviewDto();
        returnedReviewDto.setId(1);
        returnedReviewDto.setRate("4");
        returnedReviewDto.setFeedback("Car is good, but pricing could be better.");
        returnedReviewDto.setBuyerId(reviewDto1.getBuyerId());
        returnedReviewDto.setCarId(reviewDto1.getCarId());

        when(reviewDaoService.updateReview(eq(1), any(ReviewDto.class))).thenReturn(returnedReviewDto);

        mockMvc.perform(put("/v1/api/reviews/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReviewInfo))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rate").value("4"))
                .andExpect(jsonPath("$.feedback").value("Car is good, but pricing could be better."));

        verify(reviewDaoService, times(1)).updateReview(eq(1), any(ReviewDto.class));
    }

    @Test
    void deleteReview_Success() throws Exception {
        doNothing().when(reviewDaoService).deleteReview(1);

        mockMvc.perform(delete("/v1/api/reviews/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(reviewDaoService, times(1)).deleteReview(1);
    }

    @Test
    void deleteReview_NotFound() throws Exception {
        doThrow(new RuntimeException("Review not found with ID: 99")).when(reviewDaoService).deleteReview(99);

        mockMvc.perform(delete("/v1/api/reviews/{id}", 99)
                        .with(csrf()))
                .andExpect(status().isInternalServerError()); // Or the appropriate error status

        verify(reviewDaoService, times(1)).deleteReview(99);
    }
}