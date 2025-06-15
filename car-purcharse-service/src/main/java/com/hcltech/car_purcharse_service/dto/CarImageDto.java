package com.hcltech.car_purcharse_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class CarImageDto {

    private Integer id;
    private String publicId;
    private String imageUrl;
    private Integer carId; // Represents the ID of the associated Car
}