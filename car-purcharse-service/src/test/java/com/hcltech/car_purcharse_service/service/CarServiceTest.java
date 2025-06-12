package com.hcltech.car_purcharse_service.service;
import com.hcltech.car_purcharse_service.dao.service.CarDaoService;
import com.hcltech.car_purcharse_service.dto.CarDto;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CarServiceTest {

    @Mock
    private CarDaoService carDaoService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CarImageService carImageService; // Although not used in service methods, it's in the constructor

    @InjectMocks
    private CarService carService;

    // Helper method to create a Car object
    private Car createCar(Integer id, String make, String model, int year, double price, Boolean isAvailable, Seller seller) {
        Car car = new Car();
        car.setId(id);
        car.setMake(make);
        car.setModel(model);
        car.setYear(year);
        car.setPrice(price);
        car.setIsAvailable(isAvailable);
        car.setSeller(seller);
        return car;
    }

    // Helper method to create a CarDto object
    private CarDto createCarDto(Integer id, String make, String model, int year, double price, Boolean isAvailable, Integer sellerId) {
        CarDto carDto = new CarDto();
        carDto.setId(id);
        carDto.setMake(make);
        carDto.setModel(model);
        carDto.setYear(year);
        carDto.setPrice(price);
        carDto.setIsAvailable(isAvailable);
        carDto.setSellerId(sellerId);
        return carDto;
    }

    // Helper method to create a Seller object
    private Seller createSeller(Integer id, String name) {
        Seller seller = new Seller();
        seller.setId(id);
        seller.setName(name);
        return seller;
    }

    @BeforeEach
    void setUp() {
        // No specific setup needed here for @Mock and @InjectMocks due to @ExtendWith(MockitoExtension.class)
        // If you were manually initializing mocks, you'd do it here.
    }

    // --- getAll() Tests ---
    @Test
    @DisplayName("getAll should return an empty list when no cars are found")
    void getAll_noCarsFound_returnsEmptyList() {
        when(carDaoService.getAll()).thenReturn(Collections.emptyList());

        List<CarDto> result = carService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carDaoService, times(1)).getAll();
    }

    @Test
    @DisplayName("getAll should return a list of CarDto when cars are found")
    void getAll_carsFound_returnsListOfCarDto() {
        Seller seller1 = createSeller(1, "Seller A");
        Seller seller2 = createSeller(2, "Seller B");

        Car car1 = createCar(1, "Toyota", "Camry", 2020, 25000.0, true, seller1);
        Car car2 = createCar(2, "Honda", "Civic", 2019, 20000.0, false, seller2);
        Car car3 = createCar(3, "Ford", "Mustang", 2021, 35000.0, true, null); // Car without seller

        when(carDaoService.getAll()).thenReturn(Arrays.asList(car1, car2, car3));

        List<CarDto> result = carService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());

        // Verify car1 mapping
        CarDto dto1 = result.get(0);
        assertEquals(1, dto1.getId());
        assertEquals("Toyota", dto1.getMake());
        assertEquals("Camry", dto1.getModel());
        assertEquals(2020, dto1.getYear());
        assertEquals(25000.0, dto1.getPrice());
        assertTrue(dto1.getIsAvailable());
        assertEquals(1, dto1.getSellerId());

        // Verify car2 mapping
        CarDto dto2 = result.get(1);
        assertEquals(2, dto2.getId());
        assertEquals("Honda", dto2.getMake());
        assertEquals("Civic", dto2.getModel());
        assertEquals(2019, dto2.getYear());
        assertEquals(20000.0, dto2.getPrice());
        assertFalse(dto2.getIsAvailable());
        assertEquals(2, dto2.getSellerId());

        // Verify car3 mapping (without seller)
        CarDto dto3 = result.get(2);
        assertEquals(3, dto3.getId());
        assertEquals("Ford", dto3.getMake());
        assertEquals("Mustang", dto3.getModel());
        assertEquals(2021, dto3.getYear());
        assertEquals(35000.0, dto3.getPrice());
        assertTrue(dto3.getIsAvailable());
        assertNull(dto3.getSellerId());

        verify(carDaoService, times(1)).getAll();
    }

    // --- getOneById() Tests ---
    @Test
    @DisplayName("getOneById should return CarDto when car exists")
    void getOneById_carExists_returnsCarDto() {
        Seller seller = createSeller(1, "Seller A");
        Car car = createCar(1, "Toyota", "Camry", 2020, 25000.0, true, seller);

        when(carDaoService.getOneById(1)).thenReturn(car);

        CarDto result = carService.getOneById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Toyota", result.getMake());
        assertEquals(1, result.getSellerId());
        verify(carDaoService, times(1)).getOneById(1);
    }

    @Test
    @DisplayName("getOneById should return null when car does not exist")
    void getOneById_carDoesNotExist_returnsNull() {
        when(carDaoService.getOneById(99)).thenReturn(null);

        CarDto result = carService.getOneById(99);

        assertNull(result);
        verify(carDaoService, times(1)).getOneById(99);
    }

    @Test
    @DisplayName("getOneById should return CarDto with null sellerId if car has no seller")
    void getOneById_carExistsNoSeller_returnsCarDtoWithNullSellerId() {
        Car car = createCar(1, "Nissan", "Leaf", 2022, 30000.0, true, null);

        when(carDaoService.getOneById(1)).thenReturn(car);

        CarDto result = carService.getOneById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Nissan", result.getMake());
        assertNull(result.getSellerId());
        verify(carDaoService, times(1)).getOneById(1);
    }

    // --- create() Tests ---
    @Test
    @DisplayName("create should successfully create a car with a seller")
    void create_validCarDtoWithSeller_returnsCarDto() {
        Seller seller = createSeller(1, "Seller A");
        CarDto carDtoInput = createCarDto(null, "Audi", "A4", 2023, 45000.0, true, 1);
        // You don't necessarily need carToSave for the mock setup, as any(Car.class) is used.
        // However, it's good for clarity if you were using eq(carToSave) for verification.
        Car savedCar = createCar(10, "Audi", "A4", 2023, 45000.0, true, seller);

        when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));
        when(carDaoService.create(any(Car.class))).thenReturn(savedCar);

        CarDto result = carService.create(carDtoInput);

        assertNotNull(result);
        assertEquals(10, result.getId()); // Verify that ID is returned from savedCar
        assertEquals("Audi", result.getMake());
        assertEquals(1, result.getSellerId());

        verify(sellerRepository, times(1)).findById(1);

        // Corrected verify statement:
        verify(carDaoService, times(1)).create(argThat(car ->
                car.getMake().equals("Audi") &&
                        car.getModel().equals("A4") && // Added model for more precise verification
                        car.getYear()==(2023) &&   // Added year
                        car.getPrice().equals(45000.0) && // Added price
                        car.getIsAvailable().equals(true) && // Added isAvailable
                        car.getSeller() != null && // Ensure seller is not null
                        car.getSeller().getId()==(1)
        ));
        // Remove the 'Car audi =' part as verify returns the mock, not the argument.
        // Also, simplify the lambda return.
    }

    @Test
    @DisplayName("create should successfully create a car without a seller")
    void create_validCarDtoWithoutSeller_returnsCarDto() {
        CarDto carDtoInput = createCarDto(null, "BMW", "X5", 2024, 60000.0, true, null);
        Car carToSave = createCar(null, "BMW", "X5", 2024, 60000.0, true, null);
        Car savedCar = createCar(11, "BMW", "X5", 2024, 60000.0, true, null);

        // No interaction with sellerRepository when sellerId is null
        when(carDaoService.create(any(Car.class))).thenReturn(savedCar);

        CarDto result = carService.create(carDtoInput);

        assertNotNull(result);
        assertEquals(11, result.getId());
        assertEquals("BMW", result.getMake());
        assertNull(result.getSellerId());

        verifyNoInteractions(sellerRepository); // Ensure sellerRepository was not called
        verify(carDaoService, times(1)).create(argThat(car ->
                car.getMake().equals("BMW") &&
                        car.getSeller() == null
        ));
    }

    @Test
    @DisplayName("create should create car with null seller if sellerId does not exist")
    void create_carDtoWithNonExistentSellerId_setsSellerToNull() {
        Integer nonExistentSellerId = 99;
        CarDto carDtoInput = createCarDto(null, "Mercedes", "C-Class", 2023, 50000.0, true, nonExistentSellerId);
        Car savedCar = createCar(12, "Mercedes", "C-Class", 2023, 50000.0, true, null); // Saved car will have null seller

        when(sellerRepository.findById(nonExistentSellerId)).thenReturn(Optional.empty());
        when(carDaoService.create(any(Car.class))).thenReturn(savedCar);

        CarDto result = carService.create(carDtoInput);

        assertNotNull(result);
        assertEquals(12, result.getId());
        assertEquals("Mercedes", result.getMake());
        assertNull(result.getSellerId()); // SellerId should be null in the DTO

        verify(sellerRepository, times(1)).findById(nonExistentSellerId);
        verify(carDaoService, times(1)).create(argThat(car ->
                car.getMake().equals("Mercedes") &&
                        car.getSeller() == null // Verify entity passed to DAO has null seller
        ));
    }


    // --- update() Tests ---
    @Test
    @DisplayName("update should successfully update a car with seller change")
    void update_validCarDtoWithSellerChange_returnsUpdatedCarDto() {
        Seller oldSeller = createSeller(1, "Old Seller");
        Seller newSeller = createSeller(2, "New Seller");
        CarDto carDtoInput = createCarDto(1, "Toyota", "Camry", 2021, 26000.0, true, 2); // Update car with new seller
        Car updatedCarEntity = createCar(1, "Toyota", "Camry", 2021, 26000.0, true, newSeller);

        when(sellerRepository.findById(2)).thenReturn(Optional.of(newSeller));
        when(carDaoService.update(any(Car.class))).thenReturn(updatedCarEntity);

        CarDto result = carService.update(carDtoInput);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Toyota", result.getMake());
        assertEquals(26000.0, result.getPrice());
        assertEquals(2, result.getSellerId());

        verify(sellerRepository, times(1)).findById(2);
        verify(carDaoService, times(1)).update(argThat(car ->
                car.getId().equals(1) &&
                        car.getMake().equals("Toyota") &&
                        car.getSeller().getId()==(2)
        ));
    }

    @Test
    @DisplayName("update should successfully update a car without seller change")
    void update_validCarDtoWithoutSellerChange_returnsUpdatedCarDto() {
        Seller existingSeller = createSeller(1, "Existing Seller");
        CarDto carDtoInput = createCarDto(1, "Toyota", "Camry", 2021, 26000.0, true, 1); // Update car, seller remains 1
        Car updatedCarEntity = createCar(1, "Toyota", "Camry", 2021, 26000.0, true, existingSeller);

        when(sellerRepository.findById(1)).thenReturn(Optional.of(existingSeller));
        when(carDaoService.update(any(Car.class))).thenReturn(updatedCarEntity);

        CarDto result = carService.update(carDtoInput);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Toyota", result.getMake());
        assertEquals(26000.0, result.getPrice());
        assertEquals(1, result.getSellerId());

        verify(sellerRepository, times(1)).findById(1);
        verify(carDaoService, times(1)).update(any(Car.class));
    }


    @Test
    @DisplayName("update should update car with null seller if sellerId becomes null")
    void update_carDtoToNullSeller_setsSellerToNull() {
        CarDto carDtoInput = createCarDto(1, "Volvo", "S60", 2020, 30000.0, true, null);
        Car updatedCarEntity = createCar(1, "Volvo", "S60", 2020, 30000.0, true, null);

        // No interaction with sellerRepository as sellerId is null
        when(carDaoService.update(any(Car.class))).thenReturn(updatedCarEntity);

        CarDto result = carService.update(carDtoInput);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Volvo", result.getMake());
        assertNull(result.getSellerId());

        verifyNoInteractions(sellerRepository);
        verify(carDaoService, times(1)).update(argThat(car ->
                car.getMake().equals("Volvo") &&
                        car.getSeller() == null
        ));
    }

    @Test
    @DisplayName("update should update car with null seller if new sellerId does not exist")
    void update_carDtoWithNonExistentNewSellerId_setsSellerToNull() {
        Integer nonExistentSellerId = 99;
        CarDto carDtoInput = createCarDto(1, "Lexus", "ES", 2022, 40000.0, true, nonExistentSellerId);
        Car updatedCarEntity = createCar(1, "Lexus", "ES", 2022, 40000.0, true, null); // DAO will return car with null seller

        when(sellerRepository.findById(nonExistentSellerId)).thenReturn(Optional.empty());
        when(carDaoService.update(any(Car.class))).thenReturn(updatedCarEntity);

        CarDto result = carService.update(carDtoInput);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Lexus", result.getMake());
        assertNull(result.getSellerId());

        verify(sellerRepository, times(1)).findById(nonExistentSellerId);
        verify(carDaoService, times(1)).update(argThat(car ->
                car.getMake().equals("Lexus") &&
                        car.getSeller() == null
        ));
    }

    @Test
    @DisplayName("update should handle null CarDto input")
    void update_nullCarDto_returnsNull() {
        // Mock what carDaoService.update(null) would return
        when(carDaoService.update(eq(null))).thenReturn(null);

        CarDto result = carService.update(null);
        assertNull(result);

        // Verify that update was called with null argument
        verify(carDaoService, times(1)).update(eq(null));
        // No interaction with sellerRepository because toEntity(null) returns null before sellerRepository is accessed
        verifyNoInteractions(sellerRepository);
    }


    @Test
    @DisplayName("delete should return success message when car is deleted")
    void delete_carExists_returnsSuccessMessage() {
        Integer carIdToDelete = 1;
        // Assume CarDaoService.delete returns a success message or null/empty string
        when(carDaoService.delete(carIdToDelete)).thenReturn("Successfully deleted"); // Or whatever it returns

        String result = carService.delete(carIdToDelete);

        assertEquals("Delete Successful", result); // This assertion is for CarService's return value
        verify(carDaoService, times(1)).delete(carIdToDelete);
    }



    // --- getAvailableCars() Tests ---
    @Test
    @DisplayName("getAvailableCars should return empty list when no available cars are found")
    void getAvailableCars_noAvailableCars_returnsEmptyList() {
        when(carDaoService.getAvailableCars()).thenReturn(Collections.emptyList());

        List<CarDto> result = carService.getAvailableCars();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carDaoService, times(1)).getAvailableCars();
    }

    @Test
    @DisplayName("getAvailableCars should return list of available CarDto when cars are found")
    void getAvailableCars_availableCarsFound_returnsListOfCarDto() {
        Seller seller = createSeller(1, "Seller A");
        Car car1 = createCar(1, "Toyota", "Camry", 2020, 25000.0, true, seller);
        Car car2 = createCar(2, "Honda", "Civic", 2019, 20000.0, false, null); // Unavailable car
        Car car3 = createCar(3, "Ford", "Mustang", 2021, 35000.0, true, seller);

        when(carDaoService.getAvailableCars()).thenReturn(Arrays.asList(car1, car3)); // Only return available ones from DAO

        List<CarDto> result = carService.getAvailableCars();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(CarDto::getIsAvailable)); // Ensure all are available
        assertEquals(1, result.get(0).getId());
        assertEquals(3, result.get(1).getId());
        verify(carDaoService, times(1)).getAvailableCars();
    }

    // --- getCarsBySeller() Tests ---
    @Test
    @DisplayName("getCarsBySeller should return empty list when seller has no cars")
    void getCarsBySeller_sellerHasNoCars_returnsEmptyList() {
        Integer sellerId = 1;
        when(carDaoService.getCarsBySeller(sellerId)).thenReturn(Collections.emptyList());

        List<CarDto> result = carService.getCarsBySeller(sellerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carDaoService, times(1)).getCarsBySeller(sellerId);
    }

    @Test
    @DisplayName("getCarsBySeller should return list of CarDto for a specific seller")
    void getCarsBySeller_sellerHasCars_returnsListOfCarDto() {
        Seller seller = createSeller(1, "Seller A");
        Car car1 = createCar(101, "Toyota", "Corolla", 2018, 18000.0, true, seller);
        Car car2 = createCar(102, "Honda", "Accord", 2020, 28000.0, true, seller);

        when(carDaoService.getCarsBySeller(1)).thenReturn(Arrays.asList(car1, car2));

        List<CarDto> result = carService.getCarsBySeller(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.getSellerId().equals(1)));
        assertEquals(101, result.get(0).getId());
        assertEquals(102, result.get(1).getId());
        verify(carDaoService, times(1)).getCarsBySeller(1);
    }

    @Test
    @DisplayName("getCarsBySeller should return empty list if sellerId does not exist in DAO")
    void getCarsBySeller_nonExistentSellerId_returnsEmptyList() {
        Integer nonExistentSellerId = 99;
        // Assuming DAO returns an empty list if seller not found or has no cars
        when(carDaoService.getCarsBySeller(nonExistentSellerId)).thenReturn(Collections.emptyList());

        List<CarDto> result = carService.getCarsBySeller(nonExistentSellerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carDaoService, times(1)).getCarsBySeller(nonExistentSellerId);
    }

    // --- toDto(Car car) Tests (indirectly tested by other methods, but good to have direct tests for helpers) ---
    @Test
    @DisplayName("toDto(Car) should convert Car to CarDto with seller")
    void toDto_singleCarWithSeller_convertsCorrectly() {
        Seller seller = createSeller(1, "Test Seller");
        Car car = createCar(1, "BMW", "X3", 2020, 40000.0, true, seller);

        CarDto dto = carService.toDto(car); // Directly call the helper method for testing

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("BMW", dto.getMake());
        assertEquals("X3", dto.getModel());
        assertEquals(2020, dto.getYear());
        assertEquals(40000.0, dto.getPrice());
        assertTrue(dto.getIsAvailable());
        assertEquals(1, dto.getSellerId());
    }

    @Test
    @DisplayName("toDto(Car) should convert Car to CarDto without seller")
    void toDto_singleCarWithoutSeller_convertsCorrectly() {
        Car car = createCar(2, "Tesla", "Model 3", 2023, 50000.0, true, null);

        CarDto dto = carService.toDto(car);

        assertNotNull(dto);
        assertEquals(2, dto.getId());
        assertEquals("Tesla", dto.getMake());
        assertNull(dto.getSellerId());
    }

    @Test
    @DisplayName("toDto(Car) should return null for null Car input")
    void toDto_nullCar_returnsNull() {
        CarDto dto = carService.toDto((Car) null);
        assertNull(dto);
    }

    // --- toEntity(CarDto carDto) Tests (indirectly tested by create/update, but good to have direct tests for helpers) ---
    @Test
    @DisplayName("toEntity(CarDto) should convert CarDto to Car with existing seller")
    void toEntity_carDtoWithExistingSellerId_convertsCorrectly() {
        Seller seller = createSeller(1, "Test Seller");
        CarDto carDto = createCarDto(1, "Audi", "Q5", 2021, 48000.0, true, 1);

        when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));

        Car car = carService.toEntity(carDto);

        assertNotNull(car);
        assertEquals(1, car.getId());
        assertEquals("Audi", car.getMake());
        assertEquals(seller, car.getSeller()); // Verify seller object is set
        assertEquals(1, car.getSeller().getId());
        verify(sellerRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("toEntity(CarDto) should convert CarDto to Car without seller")
    void toEntity_carDtoWithoutSellerId_convertsCorrectly() {
        CarDto carDto = createCarDto(2, "Jeep", "Wrangler", 2022, 42000.0, false, null);

        Car car = carService.toEntity(carDto);

        assertNotNull(car);
        assertEquals(2, car.getId());
        assertEquals("Jeep", car.getMake());
        assertNull(car.getSeller()); // Verify seller is null
        verifyNoInteractions(sellerRepository); // No interaction with sellerRepository
    }

    @Test
    @DisplayName("toEntity(CarDto) should convert CarDto to Car with null seller if sellerId does not exist")
    void toEntity_carDtoWithNonExistentSellerId_setsSellerToNull() {
        Integer nonExistentSellerId = 99;
        CarDto carDto = createCarDto(3, "Subaru", "Outback", 2020, 28000.0, true, nonExistentSellerId);

        when(sellerRepository.findById(nonExistentSellerId)).thenReturn(Optional.empty());

        Car car = carService.toEntity(carDto);

        assertNotNull(car);
        assertEquals(3, car.getId());
        assertEquals("Subaru", car.getMake());
        assertNull(car.getSeller()); // Verify seller is null
        verify(sellerRepository, times(1)).findById(nonExistentSellerId);
    }

    @Test
    @DisplayName("toEntity(CarDto) should return null for null CarDto input")
    void toEntity_nullCarDto_returnsNull() {
        Car car = carService.toEntity(null);
        assertNull(car);
        verifyNoInteractions(sellerRepository);
    }
}