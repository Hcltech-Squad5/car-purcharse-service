package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.service.SellerService;
import com.hcltech.car_purcharse_service.model.Seller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ResponseEntity<ResponseStructure<Seller>> saveSeller(@RequestBody Seller seller) {
        log.info("Seller saved successfully");
        return sellerService.saveSeller(seller);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Seller>> findSellerById(@PathVariable int id) {
        log.info("Seller found by id");
        return sellerService.findSellerById(id);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<Seller>>> findAllSeller() {
        log.info("All seller found");
        return sellerService.findAllSeller();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Seller>> updateSeller(@RequestBody Seller seller, @PathVariable int id) {
        log.info("Seller updated ");
        return sellerService.updateSeller(seller, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<Boolean>> deleteSeller(@PathVariable int id) {
        log.info("Seller deleted successfully");
        return sellerService.deleteSeller(id);
    }
}
