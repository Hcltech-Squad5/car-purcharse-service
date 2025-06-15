package com.hcltech.car_purcharse_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class ReviewDto {
    private Integer id;
    private String rate;
    private String feedback;
    private Integer buyerId;
    private Integer carId;
}