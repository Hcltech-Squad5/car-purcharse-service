package com.hcltech.car_purcharse_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class PurchasedCarDto {
    private Integer buyerId;
    private Integer sellerId;
    private Integer carId;
    private LocalDate purchaseDate;

//    public PurchasedCarDto() {
//    }
//
//    public PurchasedCarDto(Integer buyerId, Integer sellerId, Integer carId, LocalDate purchaseDate) {
//        this.buyerId = buyerId;
//        this.sellerId = sellerId;
//        this.carId = carId;
//        this.purchaseDate = purchaseDate;
//    }
//
//    public Integer getBuyerId() {
//        return buyerId;
//    }
//
//    public void setBuyerId(Integer buyerId) {
//        this.buyerId = buyerId;
//    }
//
//    public Integer getSellerId() {
//        return sellerId;
//    }
//
//    public void setSellerId(Integer sellerId) {
//        this.sellerId = sellerId;
//    }
//
//    public Integer getCarId() {
//        return carId;
//    }
//
//    public void setCarId(Integer carId) {
//        this.carId = carId;
//    }
//
//    public LocalDate getPurchaseDate() {
//        return purchaseDate;
//    }
//
//    public void setPurchaseDate(LocalDate purchaseDate) {
//        this.purchaseDate = purchaseDate;
//    }
//
//    @Override
//    public String toString() {
//        return "PurchasedCarDto{" +
//                "buyerId=" + buyerId +
//                ", sellerId=" + sellerId +
//                ", carId=" + carId +
//                ", purchaseDate=" + purchaseDate +
//                '}';
//    }
}
