package com.hcltech.car_purcharse_service.controller;

import java.util.List;


import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.dao.service.BuyerDaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/api/buyers")
public class BuyerController {

    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);

    private final BuyerDaoService buyerDaoService;

    public BuyerController(BuyerDaoService buyerDaoService) {
        this.buyerDaoService = buyerDaoService;
    }

    @PostMapping("/create")
    public ResponseEntity<BuyerDto> createBuyer(@Valid @RequestBody BuyerDto buyerDto) {
        logger.info("Received request to create buyer with email: {}", buyerDto.getEmail());
        BuyerDto createdBuyer = buyerDaoService.createBuyer(buyerDto);
        logger.info("Buyer created successfully with ID: {}", createdBuyer.getId());
        return ResponseEntity.ok(createdBuyer);
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/{id}")
    public ResponseEntity<BuyerDto> getBuyerById(@PathVariable Integer id) {
        logger.info("Received request to get buyer by ID: {}", id);
        BuyerDto buyer = buyerDaoService.getBuyerById(id);
        logger.info("Successfully retrieved buyer with ID: {}", id);
        return ResponseEntity.ok(buyer);
    }

    @GetMapping
    public ResponseEntity<List<BuyerDto>> getAllBuyers() {
        logger.info("Received request to get all buyers");
        List<BuyerDto> buyers = buyerDaoService.getAllBuyers();
        logger.info("Successfully retrieved {} buyers", buyers.size());
        return ResponseEntity.ok(buyers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyerDto> updateBuyer(@PathVariable Integer id, @RequestBody BuyerDto buyerDto) {
        logger.info("Received request to update buyer with ID: {}", id);
        BuyerDto updatedBuyer = buyerDaoService.updateBuyer(id, buyerDto);
        logger.info("Buyer with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedBuyer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Integer id) {
        logger.info("Received request to delete buyer with ID: {}", id);
        buyerDaoService.deleteBuyer(id);
        logger.info("Buyer with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

}