package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,Integer> {


    List<Car>  findByIsAvailableTrue();

    List<Car> findAllBySellerId(Integer sellerId);
}
