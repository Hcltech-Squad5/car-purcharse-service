package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.CarImage;
import com.hcltech.car_purcharse_service.repository.CarImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarImageDaoService {

    final String ERROR_MESSAGE = "The Car Image is not found";

    private CarImageRepository carImageRepository;

    public CarImageDaoService(CarImageRepository carImageRepository) {
        this.carImageRepository = carImageRepository;
    }

    public CarImage create(CarImage carImage) {
        CarImage savedCarImage = carImageRepository.save(carImage);
        return savedCarImage;
    }

    public CarImage update(CarImage carImage) {
        CarImage savedCarImage = carImageRepository.save(carImage);
        return savedCarImage;
    }

    public void delete(Integer id) {
       carImageRepository.deleteById(id);
    }

    public CarImage getById(Integer id) {
        CarImage carImage = carImageRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        return carImage;
    }
    public List<CarImage> getByCarId(Integer carId){
        List<CarImage> carImageList = carImageRepository.findByCarId(carId);
        return carImageList;
    }
}
