package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Integer> {

    Optional<Buyer> findByEmail(String email);
}