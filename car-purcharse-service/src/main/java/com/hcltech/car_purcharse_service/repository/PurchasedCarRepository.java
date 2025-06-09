package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.PurchasedCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasedCarRepository extends JpaRepository<PurchasedCar, Integer> {
    List<PurchasedCar> findByBuyerId(Integer buyerId);

    List<PurchasedCar> findBySellerId(Integer sellerId);

    List<PurchasedCar> findByCarId(Integer carId);
}
