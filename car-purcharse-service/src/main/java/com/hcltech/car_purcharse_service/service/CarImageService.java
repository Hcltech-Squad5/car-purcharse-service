package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.model.CarImage;
import com.hcltech.car_purcharse_service.repository.CarImageRepository;
import com.hcltech.car_purcharse_service.utils.CloudinaryUtilsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarImageService {

    final String ERROR_MESSAGE = "The Car Image is not found";

    private CarImageRepository carImageRepository;

    public CarImageService(CarImageRepository carImageRepository) {
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
        CarImage carImage = carImageRepository.findById(id).orElseThrow(() -> new RuntimeException(ERROR_MESSAGE));
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
