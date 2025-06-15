package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.SellerDto;
import com.hcltech.car_purcharse_service.service.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    private static final Logger log = LoggerFactory.getLogger(SellerController.class);

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/create")
    public ResponseEntity<SellerDto> saveSeller(@RequestBody SellerDto sellerDto) {
        log.info("Seller saved successfully");
        return new ResponseEntity<>( sellerService.saveSeller(sellerDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerDto> findSellerById(@PathVariable int id) {
        log.info("Seller found by id");
        return new ResponseEntity<>( sellerService.findSellerById(id), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<SellerDto>> findAllSeller() {
        log.info("All seller found");
        return new ResponseEntity<>(sellerService.findAllSeller(),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerDto> updateSeller(@RequestBody SellerDto sellerDto, @PathVariable Integer id) {
        log.info("Seller updated ");
        return new ResponseEntity<>( sellerService.updateSeller(sellerDto, id),HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Integer id) {
        sellerService.deleteSeller(id);
        log.info("Seller deleted successfully");
        return new ResponseEntity<Void>( HttpStatus.OK);
    }
}
