package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.dto.PurchasedCarDto;
import com.hcltech.car_purcharse_service.dto.PurchasedCarResponseDto;
import com.hcltech.car_purcharse_service.exception.ResourceNotFoundException;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.PurchasedCar;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import com.hcltech.car_purcharse_service.repository.CarRepository;
import com.hcltech.car_purcharse_service.repository.PurchasedCarRepository;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchasedCarDaoServiceTest {

    @Mock
      private PurchasedCarRepository purchasedCarRepository;
    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CarRepository carRepository;

    private ModelMapper modelMapper;

    @InjectMocks
    private PurchasedCarDtoService purchasedCarDtoService;
    private PurchasedCar purchasedCar;
    private PurchasedCarDto purchasedCarDto;
    private Buyer buyer;
    private Seller seller;
    private Car car;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        purchasedCarDtoService = new PurchasedCarDtoService();
       // purchasedCarService.modelMapper = modelMapper;
        purchasedCarDtoService.purchasedCarRepository = purchasedCarRepository;
        purchasedCarDtoService.buyerRepository = buyerRepository;
        purchasedCarDtoService.sellerRepository = sellerRepository;
        purchasedCarDtoService.carRepository = carRepository;

        buyer = new Buyer();
        buyer.setId(1);

        seller = new Seller();
        seller.setId(2);

        car = new Car();
        car.setId(3);

        purchasedCar = new PurchasedCar(1, buyer, seller, car, LocalDate.of(2024, 5, 20));

        purchasedCarDto = new PurchasedCarDto(1, 2, 3, LocalDate.of(2024, 5, 20));
    }

    @Test
    void testCreatePurchasedCar() {
        when(buyerRepository.findById(1)).thenReturn(Optional.of(buyer));
        when(sellerRepository.findById(2)).thenReturn(Optional.of(seller));
        when(carRepository.findById(3)).thenReturn(Optional.of(car));
        when(purchasedCarRepository.save(any(PurchasedCar.class))).thenReturn(purchasedCar);

        PurchasedCarResponseDto response = purchasedCarDtoService.createPurchasedCar(purchasedCarDto);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(3, response.getCarId());
        verify(purchasedCarRepository, times(1)).save(any(PurchasedCar.class));
    }

    @Test
    void testGetAllPurchasedCars() {
        when(purchasedCarRepository.findAll()).thenReturn(Arrays.asList(purchasedCar));

        var result = purchasedCarDtoService.getAllPurchasedCars();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }
    @Test
    void testGetPurchasedCarById() {
        when(purchasedCarRepository.findById(1)).thenReturn(Optional.of(purchasedCar));

        PurchasedCarResponseDto response = purchasedCarDtoService.getPurchasedCarById(1);

        assertEquals(1, response.getId());
        assertEquals(3, response.getCarId());
    }

    @Test
    void testUpdatePurchasedCar() {
        when(purchasedCarRepository.findById(1)).thenReturn(Optional.of(purchasedCar));
        when(buyerRepository.findById(1)).thenReturn(Optional.of(buyer));
        when(sellerRepository.findById(2)).thenReturn(Optional.of(seller));
        when(carRepository.findById(3)).thenReturn(Optional.of(car));
        when(purchasedCarRepository.save(any(PurchasedCar.class))).thenReturn(purchasedCar);

        PurchasedCarResponseDto response = purchasedCarDtoService.updatePurchasedCar(1, purchasedCarDto);

        assertEquals(1, response.getId());
        verify(purchasedCarRepository).save(any(PurchasedCar.class));
    }

    @Test
    void testDeletePurchasedCar() {
        when(purchasedCarRepository.findById(1)).thenReturn(Optional.of(purchasedCar));

        purchasedCarDtoService.deletePurchasedCar(1);

        verify(purchasedCarRepository).delete(purchasedCar);
    }
    @Test
    void testGetPurchasedCarsByBuyerId() {
        when(purchasedCarRepository.findByBuyerId(1)).thenReturn(Arrays.asList(purchasedCar));

        var result = purchasedCarDtoService.getPurchasedCarsByBuyerId(1);

         assertEquals(1, result.size());
          assertEquals(1, result.get(0).getBuyerId());
    }
    @Test
    void testGetPurchasedCarsBySellerId() {
        when(purchasedCarRepository.findBySellerId(2)).thenReturn(Arrays.asList(purchasedCar));

        var result = purchasedCarDtoService.getPurchasedCarsBySellerId(2);
         assertEquals(1, result.size());
        assertEquals(2, result.get(0).getSellerId());
    }

    @Test
    void testGetPurchasedCarsByCarId() {
        when(purchasedCarRepository.findByCarId(3)).thenReturn(Arrays.asList(purchasedCar));

          var result = purchasedCarDtoService.getPurchasedCarsByCarId(3);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getCarId());
    }

    @Test
    void testGetPurchasedCarById_NotFound() {
        when(purchasedCarRepository.findById(100)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> purchasedCarDtoService.getPurchasedCarById(100));
    }

    @Test
    void testDeletePurchasedCar_NotFound() {
        when(purchasedCarRepository.findById(200)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> purchasedCarDtoService.deletePurchasedCar(200));
    }
}