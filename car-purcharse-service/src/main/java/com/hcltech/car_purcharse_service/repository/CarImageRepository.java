package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Integer> {
}
