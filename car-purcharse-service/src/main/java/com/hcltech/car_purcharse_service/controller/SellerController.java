package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.dto.service.SellerDtoService;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/seller")
public class SellerController {

    @Autowired
    private SellerDtoService sellerDtoService;

    @PostMapping
    public ResponseEntity<ResponseStructure<Seller>> saveSeller(@RequestBody Seller seller)
    {
        return sellerDtoService.saveSeller(seller);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Seller>> findSellerById(@PathVariable int id)
    {
        return sellerDtoService.findSellerById(id);
    }
    @GetMapping
    public  ResponseEntity<ResponseStructure<List<Seller>>> findAllSeller()
    {
        return sellerDtoService.findAllSeller();
    }

//    @GetMapping("/car")
//    public ResponseEntity<ResponseStructure<List<Car>>> findAllCar()
//    {
//        return sellerDtoService.findAllCar();
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Seller>> updateSeller(@RequestBody Seller seller,@PathVariable int id)
    {
        return sellerDtoService.updateSeller(seller,id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<Boolean>> deleteSeller(@PathVariable int id)
    {
        return sellerDtoService.deleteSeller(id);
    }
}
