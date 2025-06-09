package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
