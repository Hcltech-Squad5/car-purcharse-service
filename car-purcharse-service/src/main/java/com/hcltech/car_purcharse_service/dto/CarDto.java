package com.hcltech.car_purcharse_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class CarDto {

    Integer id;

    String make;

    String model;

    int year;

    Double price;

    Boolean isAvailable;

    Integer sellerId;
}