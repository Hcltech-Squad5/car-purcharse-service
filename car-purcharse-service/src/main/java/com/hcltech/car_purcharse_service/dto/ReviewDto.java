package com.hcltech.car_purcharse_service.dto;

public class ReviewDto {
    private Long id;
    private String rate;
    private String feedback;
    private Long buyerId;
    private Long carId;

    public ReviewDto()
    {}
    
    public ReviewDto(Long buyerId, String feedback, Long carId, Long id, String rate) {
        this.buyerId = buyerId;
        this.feedback = feedback;
        this.carId = carId;
        this.id = id;
        this.rate = rate;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}

