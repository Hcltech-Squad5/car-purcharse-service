package com.hcltech.car_purcharse_service.controller;

import java.util.List;
import java.util.Map;

import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.service.BuyerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/api/buyer")
public class BuyerController {

    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping
    public ResponseEntity<BuyerDto> createBuyer(@Valid @RequestBody BuyerDto buyerDto) {
        logger.info("Received request to create buyer with email: {}", buyerDto.getEmail());
        BuyerDto createdBuyer = buyerService.createBuyer(buyerDto);
        logger.info("Buyer created successfully with ID: {}", createdBuyer.getId());
        return ResponseEntity.ok(createdBuyer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyerDto> getBuyerById(@PathVariable Integer id) {
        logger.info("Received request to get buyer by ID: {}", id);
        BuyerDto buyer = buyerService.getBuyerById(id);
        logger.info("Successfully retrieved buyer with ID: {}", id);
        return ResponseEntity.ok(buyer);
    }

    @GetMapping
    public ResponseEntity<List<BuyerDto>> getAllBuyers() {
        logger.info("Received request to get all buyers");
        List<BuyerDto> buyers = buyerService.getAllBuyers();
        logger.info("Successfully retrieved {} buyers", buyers.size());
        return ResponseEntity.ok(buyers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyerDto> updateBuyer(@PathVariable Integer id, @RequestBody BuyerDto buyerDto) {
        logger.info("Received request to update buyer with ID: {}", id);
        BuyerDto updatedBuyer = buyerService.updateBuyer(id, buyerDto);
        logger.info("Buyer with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedBuyer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Integer id) {
        logger.info("Received request to delete buyer with ID: {}", id);
        buyerService.deleteBuyer(id);
        logger.info("Buyer with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }


////    @GetMapping("/{id}/credentials")
//    public ResponseEntity<Map<String, String>> getUserCredentials(@PathVariable Integer id) {
//        logger.info("Received request to get credentials for buyer with ID: {}", id);
//        Map<String, String> credentials = buyerService.getUserCredentials(id);
//        logger.info("Successfully retrieved credentials for buyer with ID: {}", id);
//        return ResponseEntity.ok(credentials);
//    }
}