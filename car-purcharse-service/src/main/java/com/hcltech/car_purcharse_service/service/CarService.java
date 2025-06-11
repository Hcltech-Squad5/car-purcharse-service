package com.hcltech.car_purcharse_service.service;


import com.hcltech.car_purcharse_service.dto.CarDto;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import com.hcltech.car_purcharse_service.dao.service.CarDaoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {


    private CarDaoService carDaoService;

    private SellerRepository sellerRepository;

    private CarImageService carImageService;

    public CarService(CarDaoService carDaoService, SellerRepository sellerRepository, CarImageService carImageService) {
        this.carDaoService = carDaoService;
        this.sellerRepository = sellerRepository;
        this.carImageService = carImageService;
    }

    public List<CarDto> getAll() {
        List<Car> allCars = carDaoService.getAll();
        List<CarDto> result = toDto(allCars);
        return result;
    }

    public CarDto getOneById(Integer id) {
        Car car = carDaoService.getOneById(id);
        CarDto result = toDto(car);
        return result;
    }

    public CarDto create(CarDto carDto) {

        Car car = toEntity(carDto);
        Car savedCar = carDaoService.create(car);

        CarDto result = toDto(savedCar);
        return result;
    }

    public CarDto update(CarDto carDto) {
        Car car = toEntity(carDto);
        Car savedCar = carDaoService.update(car);
        CarDto result = toDto(savedCar);
        return result;
    }

    public String delete(Integer id) {
        carDaoService.delete(id);
        return "Delete Successful";
    }

    public List<CarDto> getAvailableCars() {
        List<Car> allAvailableCars = carDaoService.getAvailableCars();
        List<CarDto> result = toDto(allAvailableCars);
        return result;
    }

    public List<CarDto> getCarsBySeller(Integer sellerId) {
        List<Car> sellerCars = carDaoService.getCarsBySeller(sellerId);
        List<CarDto> result = toDto(sellerCars);
        return result;
    }

    public List<CarDto> toDto(List<Car> cars) {
        return cars.stream()
                .map(car -> toDto(car))
                .collect(Collectors.toList());
    }

    public CarDto toDto(Car car) {
        if (car == null) {
            return null;
        }
        CarDto result = new CarDto();
        result.setId(car.getId());
        result.setMake(car.getMake());
        result.setModel(car.getModel());
        result.setYear(car.getYear());
        result.setPrice(car.getPrice());
        result.setIsAvailable(car.getIsAvailable());
        if (car.getSeller() != null) {
            result.setSellerId(car.getSeller().getId());
        } else {
            result.setSellerId(null);
        }
        return result;
    }

    public Car toEntity(CarDto carDto) {
        if (carDto == null) {
            return null;
        }
        Car result = new Car();
        result.setId(carDto.getId());
        result.setMake(carDto.getMake());
        result.setModel(carDto.getModel());
        result.setPrice(carDto.getPrice());
        result.setYear(carDto.getYear());
        result.setIsAvailable(carDto.getIsAvailable());
        if (carDto.getSellerId() != null) {
            Seller seller = sellerRepository.findById(carDto.getSellerId()).orElse(null);
            result.setSeller(seller);
        } else {
            result.setSeller(null);
        }
        return result;

    }
}
