package com.hcltech.car_purcharse_service.controller;

import java.util.List;
import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.service.BuyerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/buyer")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping
    public ResponseEntity<BuyerDto> createBuyer(@Valid @RequestBody BuyerDto buyerDto) {
        return ResponseEntity.ok(buyerService.createBuyer(buyerDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyerDto> getBuyerById(@PathVariable Integer id) {
        return ResponseEntity.ok(buyerService.getBuyerById(id));
    }

    @GetMapping
    public ResponseEntity<List<BuyerDto>> getAllBuyers() {
        return ResponseEntity.ok(buyerService.getAllBuyers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyerDto> updateBuyer(@PathVariable Integer id, @RequestBody BuyerDto buyerDto) {
        return ResponseEntity.ok(buyerService.updateBuyer(id, buyerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Integer id) {
        buyerService.deleteBuyer(id);
        return ResponseEntity.noContent().build();
    }
}