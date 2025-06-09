package com.hcltech.car_purcharse_service.dto;

import java.time.LocalDate;
public class PurchasedCarResponseDto {
    private Integer id;
    private Integer buyerId;
    private Integer sellerId;
    private Integer carId;
    private LocalDate purchaseDate;

    public PurchasedCarResponseDto() {}

    public PurchasedCarResponseDto(Integer id, Integer buyerId, Integer sellerId, Integer carId, LocalDate purchaseDate) {
        this.id = id;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.carId = carId;
        this.purchaseDate = purchaseDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "PurchasedCarResponseDto{" +
                "id=" + id +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", carId=" + carId +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
