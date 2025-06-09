package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Integer> {}