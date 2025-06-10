package com.hcltech.car_purcharse_service.dto.service;


import com.hcltech.car_purcharse_service.dto.CarDto;
import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import com.hcltech.car_purcharse_service.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarDtoService {


    private CarService carService;

    private SellerRepository sellerRepository;

    private CarImageDtoService carImageDtoService;

    public CarDtoService(CarService carService, SellerRepository sellerRepository, CarImageDtoService carImageDtoService) {
        this.carService = carService;
        this.sellerRepository = sellerRepository;
        this.carImageDtoService = carImageDtoService;
    }

    public List<CarDto> getAll() {
        List<Car> allCars = carService.getAll();
        List<CarDto> result = toDto(allCars);
        return result;
    }

    public CarDto getOneById(Integer id) {
        Car car = carService.getOneById(id);
        CarDto result = toDto(car);
        return result;
    }

    public CarDto create(CarDto carDto) {

        Car car = toEntity(carDto);
        Car savedCar = carService.create(car);

        CarDto result = toDto(savedCar);
        return result;
    }

    public CarDto update(CarDto carDto) {
        Car car = toEntity(carDto);
        Car savedCar = carService.update(car);
        CarDto result = toDto(savedCar);
        return result;
    }

    public String delete(Integer id) {
        carService.delete(id);
        return "Delete Successful";
    }

    public List<CarDto> getAvailableCars() {
        List<Car> allAvailableCars = carService.getAvailableCars();
        List<CarDto> result = toDto(allAvailableCars);
        return result;
    }

    public List<CarDto> getCarsBySeller(Integer sellerId) {
        List<Car> sellerCars = carService.getCarsBySeller(sellerId);
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
        result.setAvailable(car.getAvailable());
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
        result.setAvailable(carDto.getAvailable());
        if (carDto.getSellerId() != null) {
            Seller seller = sellerRepository.findById(carDto.getSellerId()).orElse(null);
            result.setSeller(seller);
        } else {
            result.setSeller(null);
        }
        return result;

    }
}
