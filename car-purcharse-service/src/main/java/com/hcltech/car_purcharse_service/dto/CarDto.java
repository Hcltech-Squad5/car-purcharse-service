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

//    public CarDto() {
//    }
//
//    public CarDto(Integer id, String make, String model, int year, Double price, Boolean isAvailable, Integer sellerId) {
//        this.id = id;
//        this.make = make;
//        this.model = model;
//        this.year = year;
//        this.price = price;
//        this.isAvailable = isAvailable;
//        this.sellerId = sellerId;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getMake() {
//        return make;
//    }
//
//    public void setMake(String make) {
//        this.make = make;
//    }
//
//    public String getModel() {
//        return model;
//    }
//
//    public void setModel(String model) {
//        this.model = model;
//    }
//
//    public int getYear() {
//        return year;
//    }
//
//    public void setYear(int year) {
//        this.year = year;
//    }
//
//    public Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(Double price) {
//        this.price = price;
//    }
//
//    public Boolean getAvailable() {
//        return isAvailable;
//    }
//
//    public void setAvailable(Boolean available) {
//        isAvailable = available;
//    }
//
//    public Integer getSellerId() {
//        return sellerId;
//    }
//
//    public void setSellerId(Integer sellerId) {
//        this.sellerId = sellerId;
//    }
}


