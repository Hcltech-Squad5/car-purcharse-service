package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService;
import com.hcltech.car_purcharse_service.service.CarImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
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

@WebMvcTest(CarImageController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
@TestPropertySource(properties = {"cloudinary.cloud_name=test", "cloudinary.api_key=test", "cloudinary.api_secret=test"})
class CarImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarImageService carImageService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService; // Required for JwtFilter dependency

    @Autowired
    private ObjectMapper objectMapper;

    private CarImageDto carImageDto1;
    private CarImageDto carImageDto2;

    @BeforeEach
    void setUp() {
        carImageDto1 = new CarImageDto(1, "publicId1", "http://example.com/image1.jpg", 101);
        carImageDto2 = new CarImageDto(2, "publicId2", "http://example.com/image2.jpg", 101);
    }

    @Test
    void uploadImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some image content".getBytes()
        );

        when(carImageService.upload(any(MockMultipartFile.class), eq(101))).thenReturn(carImageDto1);

        mockMvc.perform(multipart("/v1/api/car/images/upload/{id}", 101)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.publicId").value("publicId1"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/image1.jpg"))
                .andExpect(jsonPath("$.carId").value(101));

        verify(carImageService, times(1)).upload(any(MockMultipartFile.class), eq(101));
    }

    @Test
    void updateImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated_test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "updated image content".getBytes()
        );

        CarImageDto updatedCarImageDto = new CarImageDto(1, "newPublicId1", "http://example.com/new_image1.jpg", 101);
        when(carImageService.Update(any(MockMultipartFile.class), eq(1))).thenReturn(updatedCarImageDto);

        mockMvc.perform(multipart("/v1/api/car/images/update/{id}", 1)
                        .file(file)
                        .with(csrf())
                        .with(request -> {
                            // MockMvc's multipart() defaults to POST, explicitly set to PUT
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.publicId").value("newPublicId1"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/new_image1.jpg"))
                .andExpect(jsonPath("$.carId").value(101));

        verify(carImageService, times(1)).Update(any(MockMultipartFile.class), eq(1));
    }


    @Test
    void getAllImageByCar_Success() throws Exception {
        List<CarImageDto> carImages = Arrays.asList(carImageDto1, carImageDto2);
        when(carImageService.getAllImageByCar(101)).thenReturn(carImages);

        mockMvc.perform(get("/v1/api/car/images/all/{carId}", 101)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].publicId").value("publicId1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].publicId").value("publicId2"));

        verify(carImageService, times(1)).getAllImageByCar(101);
    }

    @Test
    void getImageById_Success() throws Exception {
        when(carImageService.getImageById(1)).thenReturn(carImageDto1);

        mockMvc.perform(get("/v1/api/car/images/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.publicId").value("publicId1"));

        verify(carImageService, times(1)).getImageById(1);
    }

    @Test // Re-enabled this test
    void deleteById_Success() throws Exception {
        when(carImageService.deleteById(1)).thenReturn("successfully delete image with Id1");

        // The path now correctly matches the fixed controller: /delete/{id}
        mockMvc.perform(delete("/v1/api/car/images/delete/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("successfully delete image with Id1"));

        verify(carImageService, times(1)).deleteById(1);
    }

    @Test // Re-enabled this test
    void deleteByCarId_Success() throws Exception {
        when(carImageService.deleteByCarId(101)).thenReturn("successfully delete images with of Id101");

        // The path now correctly matches the fixed controller: /delete/car/{carId}
        mockMvc.perform(delete("/v1/api/car/images/delete/car/{carId}", 101)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("successfully delete images with of Id101"));

        verify(carImageService, times(1)).deleteByCarId(101);
    }
}