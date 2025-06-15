package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.Car; // Import the Car model
import com.hcltech.car_purcharse_service.model.CarImage;
import com.hcltech.car_purcharse_service.repository.CarImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarImageDaoServiceTest {

    @Mock
    private CarImageRepository carImageRepository;

    @InjectMocks
    private CarImageDaoService carImageDaoService;

    // Helper method to create a Car object (needed for CarImage's 'car' field)
    private Car createCar(Integer id) {
        Car car = new Car();
        car.setId(id);
        return car;
    }

    private CarImage createCarImage(Integer id, Car car, String publicId, String imageUrl) {
        CarImage image = new CarImage();
        image.setId(id);
        image.setCar(car); // Set the Car object directly
        image.setPublicId(publicId);
        image.setImageUrl(imageUrl);
        return image;
    }

    // --- create() Tests ---
    @Test
    @DisplayName("create should successfully save a new car image")
    void create_success() {
        Car associatedCar = createCar(1); // Create a simple Car object
        CarImage carImageToSave = createCarImage(null, associatedCar, "publicId1", "url1");
        CarImage savedCarImage = createCarImage(1, associatedCar, "publicId1", "url1");

        when(carImageRepository.save(carImageToSave)).thenReturn(savedCarImage);

        CarImage result = carImageDaoService.create(carImageToSave);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("publicId1", result.getPublicId());
        // Verify that the saved image's car ID matches the associated car's ID
        assertNotNull(result.getCar());
        assertEquals(associatedCar.getId(), result.getCar().getId());
        verify(carImageRepository, times(1)).save(carImageToSave);
    }

    @Test
    @DisplayName("create should handle null CarImage input gracefully (repository save(null) behavior)")
    void create_nullCarImageInput_returnsNull() {
        when(carImageRepository.save(eq(null))).thenReturn(null);

        CarImage result = carImageDaoService.create(null);

        assertNull(result);
        verify(carImageRepository, times(1)).save(eq(null));
    }


    // --- update() Tests ---
    @Test
    @DisplayName("update should successfully update an existing car image")
    void update_success() {
        Car associatedCar = createCar(1);
        CarImage carImageToUpdate = createCarImage(1, associatedCar, "newPublicId", "newUrl");
        CarImage updatedCarImage = createCarImage(1, associatedCar, "newPublicId", "newUrl");

        when(carImageRepository.save(carImageToUpdate)).thenReturn(updatedCarImage);

        CarImage result = carImageDaoService.update(carImageToUpdate);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("newPublicId", result.getPublicId());
        assertNotNull(result.getCar());
        assertEquals(associatedCar.getId(), result.getCar().getId());
        verify(carImageRepository, times(1)).save(carImageToUpdate);
    }

    @Test
    @DisplayName("update should handle null CarImage input gracefully")
    void update_nullCarImageInput_returnsNull() {
        when(carImageRepository.save(eq(null))).thenReturn(null);

        CarImage result = carImageDaoService.update(null);

        assertNull(result);
        verify(carImageRepository, times(1)).save(eq(null));
    }


    // --- getById() Tests ---
    @Test
    @DisplayName("getById should return CarImage when image exists")
    void getById_carImageExists_returnsCarImage() {
        Integer imageId = 1;
        Car associatedCar = createCar(1);
        CarImage foundCarImage = createCarImage(imageId, associatedCar, "publicId", "url");

        when(carImageRepository.findById(imageId)).thenReturn(Optional.of(foundCarImage));

        CarImage result = carImageDaoService.getById(imageId);

        assertNotNull(result);
        assertEquals(imageId, result.getId());
        assertEquals("publicId", result.getPublicId());
        assertNotNull(result.getCar());
        assertEquals(associatedCar.getId(), result.getCar().getId());
        verify(carImageRepository, times(1)).findById(imageId);
    }

    @Test
    @DisplayName("getById should throw RuntimeException when image does not exist")
    void getById_carImageDoesNotExist_throwsRuntimeException() {
        Integer nonExistentImageId = 99;

        when(carImageRepository.findById(nonExistentImageId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            carImageDaoService.getById(nonExistentImageId);
        });
        assertEquals("", thrown.getMessage()); // Your service returns "" for this specific case
        verify(carImageRepository, times(1)).findById(nonExistentImageId);
    }


    // --- getByCarId() Tests ---
    @Test
    @DisplayName("getByCarId should return a list of CarImage when images exist for carId")
    void getByCarId_imagesExist_returnsListOfCarImage() {
        Integer carId = 10;
        Car associatedCar = createCar(carId); // Create a Car object matching the carId
        List<CarImage> carImages = Arrays.asList(
                createCarImage(1, associatedCar, "pid1", "url1"),
                createCarImage(2, associatedCar, "pid2", "url2")
        );

        when(carImageRepository.findByCarId(carId)).thenReturn(carImages);

        List<CarImage> result = carImageDaoService.getByCarId(carId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(carId, result.get(0).getCar().getId()); // Access getCar().getId()
        verify(carImageRepository, times(1)).findByCarId(carId);
    }

    @Test
    @DisplayName("getByCarId should return an empty list when no images exist for carId")
    void getByCarId_noImages_returnsEmptyList() {
        Integer carId = 99;

        when(carImageRepository.findByCarId(carId)).thenReturn(Collections.emptyList());

        List<CarImage> result = carImageDaoService.getByCarId(carId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carImageRepository, times(1)).findByCarId(carId);
    }

    @Test
    @DisplayName("getByCarId should return empty list when carId is null (assuming repository handles it gracefully)")
    void getByCarId_nullCarId_returnsEmptyList() {
        when(carImageRepository.findByCarId(null)).thenReturn(Collections.emptyList());

        List<CarImage> result = carImageDaoService.getByCarId(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carImageRepository, times(1)).findByCarId(null);
    }
}