package com.hcltech.car_purcharse_service.dao.service;


import com.hcltech.car_purcharse_service.dto.ReviewDto;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Review;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import com.hcltech.car_purcharse_service.repository.CarRepository;
import com.hcltech.car_purcharse_service.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewDtoService {

    private final ReviewRepository reviewRepository;
    @Autowired
    private final BuyerRepository buyerRepository;
    @Autowired
    private final CarRepository carRepository;

    public ReviewDtoService(ReviewRepository reviewRepository,
                            BuyerRepository buyerRepository,
                            CarRepository carRepository) {
        this.reviewRepository = reviewRepository;
        this.buyerRepository = buyerRepository;
        this.carRepository = carRepository;
    }

    public ReviewDto createReview(ReviewDto dto) {
        Buyer buyer = buyerRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + dto.getBuyerId()));
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + dto.getCarId()));

        Review review = new Review();
        review.setRate(dto.getRate());
        review.setFeedback(dto.getFeedback());
        review.setBuyer(buyer);
        review.setCar(car);

        Review savedReview = reviewRepository.save(review);
        return mapToResponseDto(savedReview);
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public ReviewDto getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
        return mapToResponseDto(review);
    }

    public ReviewDto updateReview(Integer id, ReviewDto dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        review.setRate(dto.getRate());
        review.setFeedback(dto.getFeedback());

        Review updatedReview = reviewRepository.save(review);
        return mapToResponseDto(updatedReview);
    }

    public void deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    private ReviewDto mapToResponseDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRate(review.getRate());
        dto.setFeedback(review.getFeedback());
        dto.setBuyerId(review.getBuyer().getId());
        dto.setCarId(review.getCar().getId());
        return dto;
    }
}
