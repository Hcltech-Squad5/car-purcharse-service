package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SellerRepository extends JpaRepository<Seller,Integer> {

}
