package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Integer> {
    Optional<CarImage> findByPublicId(String publicId);

    // Method to find all CarImage entities by a specific Car
    List<CarImage> findByCar(Car car);

    // Alternatively, if you only have the car ID and not the Car object:
    List<CarImage> findByCarId(Integer carId);
}
