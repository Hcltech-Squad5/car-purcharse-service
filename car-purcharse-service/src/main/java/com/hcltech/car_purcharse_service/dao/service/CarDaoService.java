package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarDaoService {
    private final CarRepository carRepository;

    public CarDaoService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAll() {
        return carRepository.findAll();
    }

    public Car getOneById(Integer id) {
        return carRepository.findById(id).orElse(null);
    }

    public Car create(Car car) {
        return carRepository.save(car);
    }

    public Car update(Car car) {
        return carRepository.save(car);
    }

    public String delete(Integer id) {
        carRepository.deleteById(id);
        return "Delete Successful";
    }

    public List<Car> getAvailableCars() {
        return carRepository.findByIsAvailableTrue();
    }

    public List<Car> getCarsBySeller(Integer sellerId) {
        return carRepository.findAllBySellerId(sellerId);
    }

}
