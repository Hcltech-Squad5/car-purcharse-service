package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.dto.service.SellerDtoService;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.service.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/seller")
public class SellerController {

    @Autowired
    private SellerDtoService sellerDtoService;

    private static final Logger log = LoggerFactory.getLogger(SellerController.class);

    @PostMapping("/create")
    public ResponseEntity<ResponseStructure<Seller>> saveSeller(@RequestBody Seller seller) {
        log.info("Seller saved successfully");
        return sellerDtoService.saveSeller(seller);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Seller>> findSellerById(@PathVariable int id) {
        log.info("Seller found by id");
        return sellerDtoService.findSellerById(id);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<Seller>>> findAllSeller() {
        log.info("All seller found");
        return sellerDtoService.findAllSeller();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Seller>> updateSeller(@RequestBody Seller seller, @PathVariable int id) {
        log.info("Seller updated ");
        return sellerDtoService.updateSeller(seller, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<Boolean>> deleteSeller(@PathVariable int id) {
        log.info("Seller deleted successfully");
        return sellerDtoService.deleteSeller(id);
    }
}
