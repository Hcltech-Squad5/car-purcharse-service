package com.hcltech.car_purcharse_service.dto;

// CarImageDTO.java
public class CarImageDto {

    private Integer id;
    private String publicId;
    private String imageUrl;
    private Integer carId; // Represents the ID of the associated Car

    // Constructors (default and all-args are good practice)
    public CarImageDto() {
    }

    public CarImageDto(Integer id, String publicId, String imageUrl, Integer carId) {
        this.id = id;
        this.publicId = publicId;
        this.imageUrl = imageUrl;
        this.carId = carId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    // You might also consider overriding equals() and hashCode() if you'll be using
    // DTOs in collections or for comparison.
}