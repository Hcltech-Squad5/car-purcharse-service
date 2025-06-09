package com.hcltech.car_purcharse_service.dto;

public class ReviewDto {
    private Integer id;
    private String rate;
    private String feedback;
    private Integer buyerId;
    private Integer carId;

    public ReviewDto()
    {}
    
    public ReviewDto(Integer buyerId, String feedback, Integer carId, Integer id, String rate) {
        this.buyerId = buyerId;
        this.feedback = feedback;
        this.carId = carId;
        this.id = id;
        this.rate = rate;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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

