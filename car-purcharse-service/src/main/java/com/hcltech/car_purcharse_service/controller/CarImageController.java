package com.hcltech.car_purcharse_service.controller;

import com.hcltech.car_purcharse_service.utils.CloudinaryUtilsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/car/image/")
public class CarImageController {

    private CloudinaryUtilsService cloudinaryUtilsService;

    public CarImageController(CloudinaryUtilsService cloudinaryUtilsService) {
        this.cloudinaryUtilsService = cloudinaryUtilsService;
    }

    @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> uploadImage(@Parameter(description = "the file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestPart("file") MultipartFile file) {


        try {
            return ResponseEntity.ok(cloudinaryUtilsService.uploadImage(file.getBytes()));
        } catch (IOException e) {
            Map map = new HashMap<>(Integer.parseInt("error"));

            return ResponseEntity.ok(map);
        }
    }

}
