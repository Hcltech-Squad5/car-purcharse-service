package com.hcltech.car_purcharse_service.dto;

import com.hcltech.car_purcharse_service.model.Car;

public class CombinedCarDtoAndCarImageDto {
    private Car car;
    private CarImageDto carImageDto;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public CarImageDto getCarImageDto() {
        return carImageDto;
    }

    public void setCarImageDto(CarImageDto carImageDto) {
        this.carImageDto = carImageDto;
    }
}
