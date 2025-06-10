package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.dto.CarDto;
import com.hcltech.car_purcharse_service.dto.service.CarDtoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarDtoService carDtoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CarDto testCarDto;
    private CarDto testCarDtoUpdate;
    private List<CarDto> testCarDtoList;
//    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testCarDto = new CarDto(null, "Toyota", "Camry", 2023, 30000.0, true, 1);

        testCarDtoUpdate = new CarDto(1, "Toyota", "Camry", 2024, 32000.0, true, 1);

        testCarDtoList = Arrays.asList(
                new CarDto(2, "Honda", "Civic", 2022, 25000.0, true, 1),
                new CarDto(3, "Ford", "Focus", 2021, 20000.0, true, 1)
        );

//        testFile = new MockMultipartFile(
//                "file",
//                "test.jpg",
//                "image/jpeg",
//                "some image content".getBytes()
//        );
    }

    @Test
    void createCar_Success() throws Exception {
        CarDto createdCarDto = new CarDto(1, "Toyota", "Camry", 2023, 30000.0, true, 1);
        when(carDtoService.create(any(CarDto.class))).thenReturn(createdCarDto);

        mockMvc.perform(post("/v1/api/car/create") // Updated URL and method to standard POST
                        .contentType(MediaType.APPLICATION_JSON) // Content-Type is now JSON
                        .content(objectMapper.writeValueAsString(testCarDto)) // Send CarDto as JSON body
                        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.sellerId").value(1));

        verify(carDtoService, times(1)).create(any(CarDto.class));
    }


    @Test
    void getAllCars_Success() throws Exception {
        List<CarDto> cars = Arrays.asList(testCarDto, new CarDto(2, "Nissan", "Altima", 2022, 28000.0, true, 2));
        when(carDtoService.getAll()).thenReturn(cars);

        mockMvc.perform(get("/v1/api/car/getAll") // Updated URL
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[1].make").value("Nissan"));

        verify(carDtoService, times(1)).getAll();
    }

    @Test
    void getOneById_Success() throws Exception {
        when(carDtoService.getOneById(1)).thenReturn(testCarDtoUpdate);

        mockMvc.perform(get("/v1/api/car/getOneById/{id}", 1) // Updated URL
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"));

        verify(carDtoService, times(1)).getOneById(1);
    }


    @Test
    void updateCar_Success() throws Exception {
        when(carDtoService.update(any(CarDto.class))).thenReturn(testCarDtoUpdate);

        mockMvc.perform(put("/v1/api/car/update") // Updated URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCarDtoUpdate))
                        .with(csrf()))
                .andExpect(status().isCreated()) // Controller returns 201 for update
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.year").value(2024));

        verify(carDtoService, times(1)).update(any(CarDto.class));
    }

    @Test
    void deleteCar_Success() throws Exception {
        when(carDtoService.delete(1)).thenReturn("Delete Successful");

        mockMvc.perform(delete("/v1/api/car/delete/{id}", 1) // Updated URL
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete Successful"));

        verify(carDtoService, times(1)).delete(1);
    }

   @Test
    void getAvailableCars_Success() throws Exception {
        List<CarDto> availableCars = Arrays.asList(testCarDto, new CarDto(2, "Honda", "CRV", 2023, 35000.0, true, 2));
        when(carDtoService.getAvailableCars()).thenReturn(availableCars);

        mockMvc.perform(get("/v1/api/car/getAvailableCars") // Updated URL
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].available").value(true)); // DTO has getAvailable() but JSON uses isAvailable
        verify(carDtoService, times(1)).getAvailableCars();
    }

    @Test
    void getCarsBySeller_Success() throws Exception {
        Integer sellerId = 1;
        when(carDtoService.getCarsBySeller(sellerId)).thenReturn(testCarDtoList);

        mockMvc.perform(get("/v1/api/car/seller/{sellerId}", sellerId) // Updated URL
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sellerId").value(sellerId))
                .andExpect(jsonPath("$[1].sellerId").value(sellerId));
        verify(carDtoService, times(1)).getCarsBySeller(sellerId);
    }

}