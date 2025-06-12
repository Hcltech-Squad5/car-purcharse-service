package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.CarDto;
import com.hcltech.car_purcharse_service.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/cars")
public class CarController {

    CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CarDto>> getAll(){
        return ResponseEntity.ok(carService.getAll());
    }

    @GetMapping("/getOneById/{id}")
    public ResponseEntity<CarDto> getOneById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(carService.getOneById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<CarDto> create(@RequestBody CarDto carDto){
        return ResponseEntity.status(201).body(carService.create(carDto));
    }

    @PutMapping("/update")
    public ResponseEntity<CarDto> update(@RequestBody CarDto carDto){
        return ResponseEntity.status(201).body(carService.update(carDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id){
        return ResponseEntity.ok(carService.delete(id));
    }

    @GetMapping("/getAvailableCars")
    public ResponseEntity<List<CarDto>> getAvailableCars(){
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    @GetMapping(value = "/seller/{sellerId}")
    public ResponseEntity<List<CarDto>> getCarsBySeller(@PathVariable Integer sellerId) {
        List<CarDto> cars = carService.getCarsBySeller(sellerId);
        return ResponseEntity.ok(cars);
    }

}


