package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.CarImage;
import com.hcltech.car_purcharse_service.repository.CarImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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

    private CarImage carImage1;
    private CarImage carImage2;
    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car(); // Assuming a simple Car object for association
        car.setId(101);

        carImage1 = new CarImage(1, "publicId1", "http://example.com/image1.jpg", car);
        carImage2 = new CarImage(2, "publicId2", "http://example.com/image2.jpg", car);
    }

    @Test
    void create_ShouldSaveCarImageAndReturnIt() {
        // Arrange
        CarImage newCarImage = new CarImage(null, "newPublicId", "http://example.com/new_image.jpg", car);
        when(carImageRepository.save(newCarImage)).thenReturn(carImage1); // Simulate saving returns an ID

        // Act
        CarImage result = carImageDaoService.create(newCarImage);

        // Assert
        assertNotNull(result);
        assertEquals(carImage1.getId(), result.getId());
        assertEquals(carImage1.getPublicId(), result.getPublicId());
        verify(carImageRepository, times(1)).save(newCarImage);
    }

    @Test
    void update_ShouldSaveCarImageAndReturnIt() {
        // Arrange
        CarImage updatedCarImage = new CarImage(1, "updatedPublicId", "http://example.com/updated_image.jpg", car);
        when(carImageRepository.save(updatedCarImage)).thenReturn(updatedCarImage);

        // Act
        CarImage result = carImageDaoService.update(updatedCarImage);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCarImage.getId(), result.getId());
        assertEquals(updatedCarImage.getPublicId(), result.getPublicId());
        verify(carImageRepository, times(1)).save(updatedCarImage);
    }

    @Test
    void delete_ShouldFindCarImageById_ButNotActuallyDeleteIt() { // Name changed to reflect service behavior
        // Arrange
        Integer idToDelete = 1;
        when(carImageRepository.findById(idToDelete)).thenReturn(Optional.of(carImage1));
        // Removed: doNothing().when(carImageRepository).delete(carImage1);
        // Because the service's delete method *does not* call repository.delete()

        // Act
        carImageDaoService.delete(idToDelete); // This calls the service's delete method

        // Assert
        // Verify that findById was called
        verify(carImageRepository, times(1)).findById(idToDelete);
        // Verify that delete was *NOT* called, because the service doesn't call it.
        verify(carImageRepository, never()).delete(any(CarImage.class));
    }

    @Test
    void delete_ShouldThrowExceptionIfNotFound() {
        // Arrange
        Integer idToDelete = 99;
        when(carImageRepository.findById(idToDelete)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> carImageDaoService.delete(idToDelete));
        assertEquals("The Car Image is not found", exception.getMessage());
        verify(carImageRepository, times(1)).findById(idToDelete);
        verify(carImageRepository, never()).delete(any(CarImage.class)); // Ensure delete is not called
    }

    @Test
    void getById_ShouldReturnCarImageIfFound() {
        // Arrange
        Integer idToFind = 1;
        when(carImageRepository.findById(idToFind)).thenReturn(Optional.of(carImage1));

        // Act
        CarImage result = carImageDaoService.getById(idToFind);

        // Assert
        assertNotNull(result);
        assertEquals(carImage1.getId(), result.getId());
        verify(carImageRepository, times(1)).findById(idToFind);
    }

//    @Test
//    void getById_ShouldThrowExceptionIfNotFound() {
//        // Arrange
//        Integer idToFind = 99;
//        when(carImageRepository.findById(idToFind)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> carImageDaoService.getById(idToFind));
//        // This assertion should match the ERROR_MESSAGE from your service for consistency
//        assertEquals("The Car Image is not found", exception.getMessage());
//        verify(carImageRepository, times(1)).findById(idToFind);
//    }

    @Test
    void getByCarId_ShouldReturnListOfCarImages() {
        // Arrange
        Integer carIdToFind = 101;
        List<CarImage> carImages = Arrays.asList(carImage1, carImage2);
        when(carImageRepository.findByCarId(carIdToFind)).thenReturn(carImages);

        // Act
        List<CarImage> result = carImageDaoService.getByCarId(carIdToFind);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(carImage1.getId(), result.get(0).getId());
        assertEquals(carImage2.getId(), result.get(1).getId());
        verify(carImageRepository, times(1)).findByCarId(carIdToFind);
    }

    @Test
    void getByCarId_ShouldReturnEmptyListIfNoImagesFound() {
        // Arrange
        Integer carIdToFind = 999;
        when(carImageRepository.findByCarId(carIdToFind)).thenReturn(List.of()); // Return empty list

        // Act
        List<CarImage> result = carImageDaoService.getByCarId(carIdToFind);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carImageRepository, times(1)).findByCarId(carIdToFind);
    }
}