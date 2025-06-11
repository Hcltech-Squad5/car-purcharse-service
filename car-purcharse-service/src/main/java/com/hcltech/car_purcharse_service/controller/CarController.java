package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.CarDto;
import com.hcltech.car_purcharse_service.dto.service.CarDtoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/api/car")
public class CarController {

    CarDtoService carDtoService;

    public CarController(CarDtoService carDtoService) {
        this.carDtoService = carDtoService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CarDto>> getAll(){
        return ResponseEntity.ok(carDtoService.getAll());
    }

    @GetMapping("/getOneById/{id}")
    public ResponseEntity<CarDto> getOneById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(carDtoService.getOneById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<CarDto> create(@RequestBody CarDto carDto){
        return ResponseEntity.status(201).body(carDtoService.create(carDto));
    }

    @PutMapping("/update")
    public ResponseEntity<CarDto> update(@RequestBody CarDto carDto){
        return ResponseEntity.status(201).body(carDtoService.update(carDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id){
        return ResponseEntity.ok(carDtoService.delete(id));
    }

    @GetMapping("/getAvailableCars")
    public ResponseEntity<List<CarDto>> getAvailableCars(){
        return ResponseEntity.ok(carDtoService.getAvailableCars());
    }

    @GetMapping(value = "/seller/{sellerId}")
    public ResponseEntity<List<CarDto>> getCarsBySeller(@PathVariable Integer sellerId) {
        List<CarDto> cars = carDtoService.getCarsBySeller(sellerId);
        return ResponseEntity.ok(cars);
    }

}


