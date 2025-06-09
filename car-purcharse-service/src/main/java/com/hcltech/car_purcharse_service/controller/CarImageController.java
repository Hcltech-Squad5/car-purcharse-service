package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.dto.service.CarImageDtoService;
import com.hcltech.car_purcharse_service.utils.CloudinaryUtilsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/car/image/")
public class CarImageController {

    private CarImageDtoService carImageDtoService;

    public CarImageController(CarImageDtoService carImageDtoService) {
        this.carImageDtoService = carImageDtoService;
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarImageDto> updateImage(@PathVariable Integer id, @Parameter(description = "the file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestPart("file") MultipartFile file) {

        CarImageDto carImageDto = carImageDtoService.Update(file, id);
        ResponseEntity<CarImageDto> responseEntity = new ResponseEntity<>(carImageDto, HttpStatus.CREATED);
        return responseEntity;
    }

    @PostMapping(path = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarImageDto> uploadImage(@PathVariable Integer id, @Parameter(description = "the file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestPart("file") MultipartFile file) {

        CarImageDto carImageDto = carImageDtoService.upload(file, id);
        ResponseEntity<CarImageDto> responseEntity = new ResponseEntity<>(carImageDto, HttpStatus.CREATED);
        return responseEntity;
    }

    @GetMapping("/all/{carId}")
    public ResponseEntity<List<CarImageDto>> getAllImageByCar(@PathVariable Integer carId) {

        List<CarImageDto> allImageByCar = carImageDtoService.getAllImageByCar(carId);

        return ResponseEntity.ok(allImageByCar);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<CarImageDto> getImageById(@PathVariable Integer id) {

        CarImageDto imageById = carImageDtoService.getImageById(id);

        return ResponseEntity.ok(imageById);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {

        String message = carImageDtoService.deleteById(id);

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete/{carId}")
    public ResponseEntity<String> deleteByCarId(@PathVariable Integer carId) {
        String message = carImageDtoService.deleteByCarId(carId);

        return ResponseEntity.ok(message);
    }

}


