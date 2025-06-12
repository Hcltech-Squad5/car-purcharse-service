package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.PurchasedCarDto;
import com.hcltech.car_purcharse_service.dto.PurchasedCarResponseDto;
import com.hcltech.car_purcharse_service.dao.service.PurchasedCarDaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/api/purchased-cars")
public class PurchasedCarController {
private static final Logger logger = LoggerFactory.getLogger(PurchasedCarController.class);
    @Autowired
    private PurchasedCarDaoService purchasedCarDaoService;


    @PostMapping("/")
    public ResponseEntity<PurchasedCarResponseDto> createPurchasedCar(@RequestBody PurchasedCarDto dto) {
        logger.info("Received request to save purchased car details : {}", dto);
        PurchasedCarResponseDto created = purchasedCarDaoService.createPurchasedCar(dto);
        logger.info("Saved succefully: {}", created.getId());
        return ResponseEntity.ok(created);
    }


    @GetMapping("/all")
    public ResponseEntity<List<PurchasedCarResponseDto>> getAllPurchasedCars() {
        List<PurchasedCarResponseDto> list = purchasedCarDaoService.getAllPurchasedCars();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PurchasedCarResponseDto> getPurchasedCarById(@PathVariable Integer id) {
        PurchasedCarResponseDto result = purchasedCarDaoService.getPurchasedCarById(id);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/{id}")
    public ResponseEntity<PurchasedCarResponseDto> updatePurchasedCar(
            @PathVariable Integer id,
            @RequestBody PurchasedCarDto dto) {
        PurchasedCarResponseDto updated = purchasedCarDaoService.updatePurchasedCar(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchasedCar(@PathVariable Integer id) {
        purchasedCarDaoService.deletePurchasedCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<PurchasedCarResponseDto>> getPurchasedCarsByBuyerId(@PathVariable Integer buyerId) {
        List<PurchasedCarResponseDto> list = purchasedCarDaoService.getPurchasedCarsByBuyerId(buyerId);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<PurchasedCarResponseDto>> getPurchasedCarsBySellerId(@PathVariable Integer sellerId) {
        List<PurchasedCarResponseDto> list = purchasedCarDaoService.getPurchasedCarsBySellerId(sellerId);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/car/{carId}")
    public ResponseEntity<List<PurchasedCarResponseDto>> getPurchasedCarsByCarId(@PathVariable Integer carId) {
        List<PurchasedCarResponseDto> list = purchasedCarDaoService.getPurchasedCarsByCarId(carId);
        return ResponseEntity.ok(list);
    }
}
