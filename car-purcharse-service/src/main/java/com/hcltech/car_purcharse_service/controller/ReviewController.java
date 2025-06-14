package com.hcltech.car_purcharse_service.controller;


import com.hcltech.car_purcharse_service.dto.ReviewDto;
import com.hcltech.car_purcharse_service.dao.service.ReviewDaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/reviews")
public class ReviewController {

    private final ReviewDaoService reviewDaoService;

    public ReviewController(ReviewDaoService reviewDaoService) {
        this.reviewDaoService = reviewDaoService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewdto) {
        return ResponseEntity.ok(reviewDaoService.createReview(reviewdto));
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewDaoService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(reviewDaoService.getReviewById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Integer id,
                                                  @RequestBody ReviewDto reviewdto) {
        return ResponseEntity.ok(reviewDaoService.updateReview(id, reviewdto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        reviewDaoService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
