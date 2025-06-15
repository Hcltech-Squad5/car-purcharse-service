package com.hcltech.car_purcharse_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class PurchasedCarResponseDto {
    private Integer id;
    private Integer buyerId;
    private Integer sellerId;
    private Integer carId;
    private LocalDate purchaseDate;
}