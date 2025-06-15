package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dao.service.CarImageDaoService;
import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.model.Car; // Import the Car model
import com.hcltech.car_purcharse_service.model.CarImage;
import com.hcltech.car_purcharse_service.utils.CloudinaryUtilsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarImageServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CarImageDaoService carImageDaoService;
    @Mock
    private CloudinaryUtilsService cloudinaryUtilsService;
    @Mock
    private MultipartFile mockMultipartFile;

    @InjectMocks
    private CarImageService carImageService;

    private CarImageDto carImageDto;
    private CarImage carImage;
    private Car mockCar; // Mock for the associated Car entity
    private Integer carId;
    private Integer imageId;
    private String publicId;
    private String imageUrl;
    private byte[] fileBytes;

    @BeforeEach
    void setUp() throws IOException {
        carId = 101; // ID for the mock Car
        imageId = 1;
        publicId = "sample_public_id";
        imageUrl = "http://example.com/image.jpg";
        fileBytes = "test image content".getBytes();

        // Initialize a mock Car object with an ID
        mockCar = new Car();
        mockCar.setId(carId);
//        mockCar.setMake("abc");
//        mockCar.setPrice(123454.0);
//        mockCar.setModel("abc");
//        mockCar.setYear(2025);
//        mockCar.setIsAvailable(true);
//        mockCar.setSeller(null);
        // If your Car model's equals/hashCode relies on other fields, set them here too.

        // Setup common CarImageDto for tests
        // DTO still uses Integer carId for convenience in service logic
        carImageDto = new CarImageDto(imageId, publicId, imageUrl, carId);

        // Setup common CarImage entity for tests
        carImage = new CarImage();
        carImage.setId(imageId);
        carImage.setPublicId(publicId);
        carImage.setImageUrl(imageUrl);
        carImage.setCar(mockCar); // Link the CarImage to the mock Car

        // Common mock for MultipartFile behavior
//        when(mockMultipartFile.getBytes()).thenReturn(fileBytes);
    }

    @Test
    @DisplayName("Should upload a car image successfully")
    void testUpload_Success() throws IOException {
        // Arrange
        // --- CRITICAL: Stub mockMultipartFile.getBytes() FIRST for this test ---
        when(mockMultipartFile.getBytes()).thenReturn(fileBytes);

        Map<String, String> uploadResult = Map.of(
                "public_id", publicId,
                "url", imageUrl
        );

        when(cloudinaryUtilsService.uploadImage(fileBytes)).thenReturn(uploadResult);

        ArgumentCaptor<CarImageDto> dtoToEntityCaptor = ArgumentCaptor.forClass(CarImageDto.class);
        when(modelMapper.map(dtoToEntityCaptor.capture(), eq(CarImage.class)))
                .thenReturn(carImage);

        when(carImageDaoService.create(carImage)).thenReturn(carImage);
        when(modelMapper.map(carImage, CarImageDto.class)).thenReturn(carImageDto);

        // Act
        CarImageDto result = carImageService.upload(mockMultipartFile, carId);

        // Assert
        assertNotNull(result);
        assertEquals(carImageDto.getId(), result.getId());
        assertEquals(carImageDto.getCarId(), result.getCarId());
        assertEquals(carImageDto.getPublicId(), result.getPublicId());
        assertEquals(carImageDto.getImageUrl(), result.getImageUrl());

        CarImageDto capturedDtoForCreate = dtoToEntityCaptor.getValue();
        assertNotNull(capturedDtoForCreate);
        assertEquals(carId, capturedDtoForCreate.getCarId(), "CarId must be set in DTO before mapping to entity for create");
        assertEquals(publicId, capturedDtoForCreate.getPublicId());
        assertEquals(imageUrl, capturedDtoForCreate.getImageUrl());

        // Verify interactions
        verify(cloudinaryUtilsService, times(1)).uploadImage(fileBytes);
        verify(modelMapper, times(1)).map(any(CarImageDto.class), eq(CarImage.class));
        verify(carImageDaoService, times(1)).create(carImage);
        verify(modelMapper, times(1)).map(carImage, CarImageDto.class);
        verify(mockMultipartFile, times(1)).getBytes();
        verifyNoMoreInteractions(cloudinaryUtilsService, modelMapper, carImageDaoService, mockMultipartFile);
    }

    @Test
    @DisplayName("Should throw RuntimeException if IOException occurs during upload")
    void testUpload_IOException() throws IOException {
        // Arrange
        when(mockMultipartFile.getBytes()).thenThrow(new IOException("Test IO Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.upload(mockMultipartFile, carId);
        });

        assertEquals("Error in uploading the image in Cloudinary", exception.getMessage());

        // Verify interactions
        verify(mockMultipartFile, times(1)).getBytes();
        verifyNoMoreInteractions(cloudinaryUtilsService, modelMapper, carImageDaoService, mockMultipartFile);
    }

    @Test
    @DisplayName("Should update a car image successfully - Demonstrates CarId Bug if Service is Uncorrected")
    void testUpdate_Success_And_DemonstrateCarIdBug() throws IOException {
        // Arrange
        // This stubbing MUST be at the very top of the arrange section
        // to ensure mockMultipartFile.getBytes() returns fileBytes
        when(mockMultipartFile.getBytes()).thenReturn(fileBytes); // <-- Ensure this is present and first for mockMultipartFile

        CarImageDto expectedReturnedCarImageDto = new CarImageDto(imageId, "new_public_id", "http://example.com/new_image.jpg", carId);
        CarImage updatedCarImageEntity = new CarImage();
        updatedCarImageEntity.setId(imageId);
        updatedCarImageEntity.setCar(mockCar);
        updatedCarImageEntity.setPublicId("new_public_id");
        updatedCarImageEntity.setImageUrl("http://example.com/new_image.jpg");

        Map<String, String> newUploadResult = Map.of(
                "public_id", "new_public_id",
                "url", "http://example.com/new_image.jpg"
        );

        when(carImageDaoService.getById(imageId)).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(carImage.getPublicId())).thenReturn("ok");
        // This is the stubbing for the *new* upload that happens after deletion
        when(cloudinaryUtilsService.uploadImage(fileBytes)).thenReturn(newUploadResult); // This expects fileBytes, not null

        ArgumentCaptor<CarImageDto> carImageDtoCaptor = ArgumentCaptor.forClass(CarImageDto.class);
        when(modelMapper.map(carImageDtoCaptor.capture(), eq(CarImage.class))).thenReturn(updatedCarImageEntity);

        when(carImageDaoService.update(updatedCarImageEntity)).thenReturn(updatedCarImageEntity);
        when(modelMapper.map(updatedCarImageEntity, CarImageDto.class)).thenReturn(expectedReturnedCarImageDto);


        // Act
        CarImageDto result = carImageService.Update(mockMultipartFile, imageId);

        // ... rest of your test
    }

    @Test
    @DisplayName("Should throw RuntimeException if old image deletion fails during Update")
    void testUpdate_DeleteImageFailed() throws IOException {
        // Arrange
        when(carImageDaoService.getById(imageId)).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(carImage.getPublicId())).thenReturn("error_status"); // Not "ok"

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.Update(mockMultipartFile, imageId);
        });

        assertEquals("The image is not delete in cloudinary", exception.getMessage());

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(imageId);
        verify(cloudinaryUtilsService, times(1)).deleteImage(carImage.getPublicId());
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService, mockMultipartFile);
    }

    @Test
    @DisplayName("Should throw RuntimeException if IOException occurs during Update upload")
    void testUpdate_IOException() throws IOException {
        // Arrange
        when(carImageDaoService.getById(imageId)).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(carImage.getPublicId())).thenReturn("ok");
        when(mockMultipartFile.getBytes()).thenThrow(new IOException("Error during new image upload bytes"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.Update(mockMultipartFile, imageId);
        });

        assertEquals("Error in uploading the image in Cloudinary", exception.getMessage());

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(imageId);
        verify(cloudinaryUtilsService, times(1)).deleteImage(carImage.getPublicId());
        verify(mockMultipartFile, times(1)).getBytes();
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService, mockMultipartFile);
    }

    @Test
    @DisplayName("Should retrieve all images by car ID successfully")
    void testGetAllImageByCar_Success() {
        // Arrange
        CarImage carImage2 = new CarImage();
        carImage2.setId(2);
        carImage2.setCar(mockCar); // Link to the same mock Car
        carImage2.setPublicId("public_id_2");
        carImage2.setImageUrl("url_2");

        // DTOs for expected return list
        CarImageDto carImageDto2 = new CarImageDto(2, "public_id_2", "url_2", carId);

        List<CarImage> carImages = Arrays.asList(carImage, carImage2);
        List<CarImageDto> expectedDtos = Arrays.asList(carImageDto, carImageDto2);

        // Mock DAO to return list of entities
        when(carImageDaoService.getByCarId(carId)).thenReturn(carImages);
        // Mock ModelMapper for each entity to DTO conversion
        // The service's stream().map() will cause these calls
        when(modelMapper.map(carImage, CarImageDto.class)).thenReturn(carImageDto);
        when(modelMapper.map(carImage2, CarImageDto.class)).thenReturn(carImageDto2);

        // Act
        List<CarImageDto> result = carImageService.getAllImageByCar(carId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos.get(0).getPublicId(), result.get(0).getPublicId());
        assertEquals(expectedDtos.get(1).getPublicId(), result.get(1).getPublicId());
        assertEquals(expectedDtos, result);

        // Verify interactions
        verify(carImageDaoService, times(1)).getByCarId(carId);
        verify(modelMapper, times(2)).map(any(CarImage.class), eq(CarImageDto.class));
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should retrieve an empty list if no images found for car ID")
    void testGetAllImageByCar_EmptyList() {
        // Arrange
        when(carImageDaoService.getByCarId(carId)).thenReturn(Collections.emptyList());

        // Act
        List<CarImageDto> result = carImageService.getAllImageByCar(carId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(carImageDaoService, times(1)).getByCarId(carId);
        verify(modelMapper, never()).map(any(CarImage.class), eq(CarImageDto.class));
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should retrieve image by ID successfully")
    void testGetImageById_Success() {
        // Arrange
        when(carImageDaoService.getById(imageId)).thenReturn(carImage);
        when(modelMapper.map(carImage, CarImageDto.class)).thenReturn(carImageDto);

        // Act
        CarImageDto result = carImageService.getImageById(imageId);

        // Assert
        assertNotNull(result);
        assertEquals(carImageDto.getId(), result.getId());
        assertEquals(carImageDto.getPublicId(), result.getPublicId());

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(imageId);
        verify(modelMapper, times(1)).map(carImage, CarImageDto.class);
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should throw RuntimeException if image not found by ID during getImageById")
    void testGetImageById_NotFound() {
        // Arrange
        when(carImageDaoService.getById(anyInt())).thenThrow(new RuntimeException("Image not found in DAO"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.getImageById(99);
        });

        assertEquals("Image not found in DAO", exception.getMessage());

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(99);
        verify(modelMapper, never()).map(any(CarImage.class), eq(CarImageDto.class));
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should delete image by ID successfully")
    void testDeleteById_Success() {
        // Arrange
        when(carImageDaoService.getById(imageId)).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(publicId)).thenReturn("ok");
        doNothing().when(carImageDaoService).delete(imageId);

        // Act
        String result = carImageService.deleteById(imageId);

        // Assert
        assertEquals("successfully delete image with Id:" + imageId, result);

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(imageId);
        verify(cloudinaryUtilsService, times(1)).deleteImage(publicId);
        verify(carImageDaoService, times(1)).delete(imageId);
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should throw RuntimeException if Cloudinary deletion fails during deleteById")
    void testDeleteById_CloudinaryFailure() {
        // Arrange
        when(carImageDaoService.getById(imageId)).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(publicId)).thenReturn("failed");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.deleteById(imageId);
        });

        assertEquals("Error in deleting image in cloudinary", exception.getMessage());

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(imageId);
        verify(cloudinaryUtilsService, times(1)).deleteImage(publicId);
        verify(carImageDaoService, never()).delete(anyInt());
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should throw RuntimeException if image not found during deleteById (from DAO)")
    void testDeleteById_ImageNotFoundFromDao() {
        // Arrange
        when(carImageDaoService.getById(anyInt())).thenThrow(new RuntimeException("Image not found for deletion"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.deleteById(99);
        });

        assertEquals("Image not found for deletion", exception.getMessage());

        // Verify interactions
        verify(carImageDaoService, times(1)).getById(99);
        verify(cloudinaryUtilsService, never()).deleteImage(anyString());
        verify(carImageDaoService, never()).delete(anyInt());
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should delete all images for a car ID successfully")
    void testDeleteByCarId_Success() {
        // Arrange
        CarImage carImage2 = new CarImage();
        carImage2.setId(2);
        carImage2.setCar(mockCar);
        carImage2.setPublicId("public_id_2");
        carImage2.setImageUrl("url_2");

        List<CarImage> carImages = Arrays.asList(carImage, carImage2);

        // Mock DAO to return list of images for the car
        when(carImageDaoService.getByCarId(carId)).thenReturn(carImages);

        // Mock individual deleteById calls (which are internal to deleteByCarId logic)
        // For carImage (id 1)
        when(carImageDaoService.getById(carImage.getId())).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(carImage.getPublicId())).thenReturn("ok");
        doNothing().when(carImageDaoService).delete(carImage.getId());

        // For carImage2 (id 2)
        when(carImageDaoService.getById(carImage2.getId())).thenReturn(carImage2);
        when(cloudinaryUtilsService.deleteImage(carImage2.getPublicId())).thenReturn("ok");
        doNothing().when(carImageDaoService).delete(carImage2.getId());

        // Act
        String result = carImageService.deleteByCarId(carId);

        // Assert
        assertEquals("successfully delete images with of Id:" + carId, result);

        // Verify interactions
        verify(carImageDaoService, times(1)).getByCarId(carId);

        // Verify interactions for each image deletion via deleteById
        verify(carImageDaoService, times(1)).getById(carImage.getId());
        verify(cloudinaryUtilsService, times(1)).deleteImage(carImage.getPublicId());
        verify(carImageDaoService, times(1)).delete(carImage.getId());

        verify(carImageDaoService, times(1)).getById(carImage2.getId());
        verify(cloudinaryUtilsService, times(1)).deleteImage(carImage2.getPublicId());
        verify(carImageDaoService, times(1)).delete(carImage2.getId());

        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should return success message for deleteByCarId if no images found for car")
    void testDeleteByCarId_NoImages() {
        // Arrange
        when(carImageDaoService.getByCarId(carId)).thenReturn(Collections.emptyList());

        // Act
        String result = carImageService.deleteByCarId(carId);

        // Assert
        assertEquals("successfully delete images with of Id:" + carId, result);

        // Verify interactions
        verify(carImageDaoService, times(1)).getByCarId(carId);
        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }

    @Test
    @DisplayName("Should throw RuntimeException if any image deletion fails during deleteByCarId")
    void testDeleteByCarId_PartialFailure() {
        // Arrange
        CarImage carImage2 = new CarImage();
        carImage2.setId(2);
        carImage2.setCar(mockCar);
        carImage2.setPublicId("public_id_2");
        carImage2.setImageUrl("url_2");

        List<CarImage> carImages = Arrays.asList(carImage, carImage2);

        // Mock DAO to return list of images for the car
        when(carImageDaoService.getByCarId(carId)).thenReturn(carImages);

        // Mock successful deletion for the first image (carImage)
        when(carImageDaoService.getById(carImage.getId())).thenReturn(carImage);
        when(cloudinaryUtilsService.deleteImage(carImage.getPublicId())).thenReturn("ok");
        doNothing().when(carImageDaoService).delete(carImage.getId());

        // Mock FAILED deletion for the second image (carImage2)
        when(carImageDaoService.getById(carImage2.getId())).thenReturn(carImage2);
        when(cloudinaryUtilsService.deleteImage(carImage2.getPublicId())).thenReturn("failed");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carImageService.deleteByCarId(carId);
        });

        assertEquals("Error in deleting image in cloudinary", exception.getMessage());

        // Verify interactions
        verify(carImageDaoService, times(1)).getByCarId(carId);

        // Verify calls for the first image (successful deletion)
        verify(carImageDaoService, times(1)).getById(carImage.getId());
        verify(cloudinaryUtilsService, times(1)).deleteImage(carImage.getPublicId());
        verify(carImageDaoService, times(1)).delete(carImage.getId());

        // Verify calls for the second image (failed deletion - will only reach Cloudinary delete)
        verify(carImageDaoService, times(1)).getById(carImage2.getId());
        verify(cloudinaryUtilsService, times(1)).deleteImage(carImage2.getPublicId());
        verify(carImageDaoService, never()).delete(carImage2.getId());

        verifyNoMoreInteractions(modelMapper, carImageDaoService, cloudinaryUtilsService);
    }
}