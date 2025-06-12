package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.service.CarImageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/api/car/images/")
public class CarImageController {

    private CarImageService carImageService;

    public CarImageController(CarImageService carImageService) {
        this.carImageService = carImageService;
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarImageDto> updateImage(@PathVariable Integer id, @Parameter(description = "the file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestPart("file") MultipartFile file) {

        CarImageDto carImageDto = carImageService.Update(file, id);
        ResponseEntity<CarImageDto> responseEntity = new ResponseEntity<>(carImageDto, HttpStatus.CREATED);
        return responseEntity;
    }

    @PostMapping(path = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarImageDto> uploadImage(@PathVariable Integer id, @Parameter(description = "the file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestPart("file") MultipartFile file) {

        CarImageDto carImageDto = carImageService.upload(file, id);
        ResponseEntity<CarImageDto> responseEntity = new ResponseEntity<>(carImageDto, HttpStatus.CREATED);
        return responseEntity;
    }

    @GetMapping("/all/{carId}")
    public ResponseEntity<List<CarImageDto>> getAllImageByCar(@PathVariable Integer carId) {

        List<CarImageDto> allImageByCar = carImageService.getAllImageByCar(carId);

        return ResponseEntity.ok(allImageByCar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarImageDto> getImageById(@PathVariable Integer id) {

        CarImageDto imageById = carImageService.getImageById(id);

        return ResponseEntity.ok(imageById);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {

        String message = carImageService.deleteById(id);

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete/car/{carId}")
    public ResponseEntity<String> deleteByCarId(@PathVariable Integer carId) {
        String message = carImageService.deleteByCarId(carId);

        return ResponseEntity.ok(message);
    }

}


