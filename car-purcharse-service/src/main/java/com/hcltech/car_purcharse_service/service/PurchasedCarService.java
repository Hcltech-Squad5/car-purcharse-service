package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.controller.PurchasedCarController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchasedCarService {

    @Autowired
    PurchasedCarRepository purchasedCarRepository;

    @Autowired
    BuyerRepository buyerRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    CarRepository carRepository;

    private static final Logger logger = LoggerFactory.getLogger(PurchasedCarController.class);

    public PurchasedCarResponseDto createPurchasedCar(PurchasedCarDto dto) {
        logger.info("Received request to save purchased car details : {}", dto);

        Buyer buyer = buyerRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + dto.getBuyerId()));
        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + dto.getSellerId()));
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + dto.getCarId()));

        PurchasedCar entity = new PurchasedCar();
        entity.setBuyer(buyer);
        entity.setSeller(seller);
        entity.setCar(car);
        entity.setPurchaseDate(dto.getPurchaseDate());

        PurchasedCar saved = purchasedCarRepository.save(entity);
        logger.info("Saved successfully: {}", saved.getId());

        return new PurchasedCarResponseDto(
                saved.getId(),
                saved.getBuyer().getId(),
                saved.getSeller().getId(),
                saved.getCar().getId(),
                saved.getPurchaseDate()
        );
    }

    public List<PurchasedCarResponseDto> getAllPurchasedCars() {
        return purchasedCarRepository.findAll()
                .stream()
                .map(pc -> new PurchasedCarResponseDto(
                        pc.getId(),
                        pc.getBuyer().getId(),
                        pc.getSeller().getId(),
                        pc.getCar().getId(),
                        pc.getPurchaseDate()
                ))
                .collect(Collectors.toList());
    }

    public PurchasedCarResponseDto getPurchasedCarById(Integer id) {
        PurchasedCar pc = purchasedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchasedCar not found with ID: " + id));

        return new PurchasedCarResponseDto(
                pc.getId(),
                pc.getBuyer().getId(),
                pc.getSeller().getId(),
                pc.getCar().getId(),
                pc.getPurchaseDate()
        );
    }

    public PurchasedCarResponseDto updatePurchasedCar(Integer id, PurchasedCarDto dto) {
        PurchasedCar existing = purchasedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchasedCar not found with ID: " + id));

        Buyer buyer = buyerRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + dto.getBuyerId()));
        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + dto.getSellerId()));
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + dto.getCarId()));

        existing.setBuyer(buyer);
        existing.setSeller(seller);
        existing.setCar(car);
        existing.setPurchaseDate(dto.getPurchaseDate());

        PurchasedCar updated = purchasedCarRepository.save(existing);

        return new PurchasedCarResponseDto(
                updated.getId(),
                updated.getBuyer().getId(),
                updated.getSeller().getId(),
                updated.getCar().getId(),
                updated.getPurchaseDate()
        );
    }

    public void deletePurchasedCar(Integer id) {
        PurchasedCar pc = purchasedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchasedCar not found with ID: " + id));
        purchasedCarRepository.delete(pc);
    }

    public List<PurchasedCarResponseDto> getPurchasedCarsByBuyerId(Integer buyerId) {
        return purchasedCarRepository.findByBuyerId(buyerId)
                .stream()
                .map(pc -> new PurchasedCarResponseDto(
                        pc.getId(),
                        pc.getBuyer().getId(),
                        pc.getSeller().getId(),
                        pc.getCar().getId(),
                        pc.getPurchaseDate()
                ))
                .collect(Collectors.toList());
    }

    public List<PurchasedCarResponseDto> getPurchasedCarsBySellerId(Integer sellerId) {
        return purchasedCarRepository.findBySellerId(sellerId)
                .stream()
                .map(pc -> new PurchasedCarResponseDto(
                        pc.getId(),
                        pc.getBuyer().getId(),
                        pc.getSeller().getId(),
                        pc.getCar().getId(),
                        pc.getPurchaseDate()
                ))
                .collect(Collectors.toList());
    }

    public List<PurchasedCarResponseDto> getPurchasedCarsByCarId(Integer carId) {
        return purchasedCarRepository.findByCarId(carId)
                .stream()
                .map(pc -> new PurchasedCarResponseDto(
                        pc.getId(),
                        pc.getBuyer().getId(),
                        pc.getSeller().getId(),
                        pc.getCar().getId(),
                        pc.getPurchaseDate()
                ))
                .collect(Collectors.toList());
    }
}