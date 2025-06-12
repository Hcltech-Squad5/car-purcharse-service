package com.hcltech.car_purcharse_service.dao.service;





import com.hcltech.car_purcharse_service.dto.ReviewDto;
import com.hcltech.car_purcharse_service.model.Review;
import com.hcltech.car_purcharse_service.model.Buyer; // Needed for Review entity
import com.hcltech.car_purcharse_service.model.Car;   // Needed for Review entity
import com.hcltech.car_purcharse_service.repository.ReviewRepository;
import com.hcltech.car_purcharse_service.repository.BuyerRepository; // To mock finding Buyer
import com.hcltech.car_purcharse_service.repository.CarRepository;     // To mock finding Car
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewDaoServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock // Mocking BuyerRepository as Review has a ManyToOne relationship with Buyer
    private BuyerRepository buyerRepository;

    @Mock // Mocking CarRepository as Review has a ManyToOne relationship with Car
    private CarRepository carRepository;

    @InjectMocks
    private ReviewDaoService reviewService; // Assuming you have a ReviewService class

    private Review review;
    private ReviewDto reviewDto;
    private Buyer testBuyer; // To simulate existing buyer
    private Car testCar;     // To simulate existing car

    @BeforeEach
    void setUp() {
        // Setup mock Buyer and Car entities
        testBuyer = new Buyer();
        testBuyer.setId(101);
        testBuyer.setFirstName("Test");
        testBuyer.setLastName("Buyer");
        testBuyer.setEmail("test.buyer@example.com");
        testBuyer.setPhoneNumber("1234567890");

        testCar = new Car();
        testCar.setId(201);
        testCar.setMake("TestMake");
        testCar.setModel("TestModel");
        testCar.setYear(2022); // Assuming String

        // Setup Review entity
        review = new Review();
        review.setId(1);
        review.setRate("5");
        review.setFeedback("Excellent car, very comfortable and reliable!");
        review.setBuyer(testBuyer); // Link to mock buyer
        review.setCar(testCar);     // Link to mock car

        // Setup ReviewDto for creation/update
        reviewDto = new ReviewDto();
        reviewDto.setId(1);
        reviewDto.setRate("5");
        reviewDto.setFeedback("Excellent car, very comfortable and reliable!");
        reviewDto.setBuyerId(testBuyer.getId()); // Include IDs for relationships
        reviewDto.setCarId(testCar.getId());
    }

    @Test
    @DisplayName("Should create a review successfully")
    void shouldCreateReviewSuccessfully() {
        // Mock repository calls for creating a review
        when(buyerRepository.findById(testBuyer.getId())).thenReturn(Optional.of(testBuyer));
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDto createdReviewDto = reviewService.createReview(reviewDto);

        assertThat(createdReviewDto).isNotNull();
        assertThat(createdReviewDto.getId()).isEqualTo(1);
        assertThat(createdReviewDto.getRate()).isEqualTo("5");
        assertThat(createdReviewDto.getFeedback()).isEqualTo("Excellent car, very comfortable and reliable!");
        assertThat(createdReviewDto.getBuyerId()).isEqualTo(testBuyer.getId());
        assertThat(createdReviewDto.getCarId()).isEqualTo(testCar.getId());

        verify(buyerRepository, times(1)).findById(testBuyer.getId());
        verify(carRepository, times(1)).findById(testCar.getId());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if buyer not found during review creation")
    void shouldThrowExceptionIfBuyerNotFoundOnCreate() {
        when(buyerRepository.findById(anyInt())).thenReturn(Optional.empty()); // Buyer not found

        assertThrows(RuntimeException.class, () -> reviewService.createReview(reviewDto));

        verify(buyerRepository, times(1)).findById(anyInt());
        verify(carRepository, never()).findById(anyInt()); // Car lookup should not happen
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if car not found during review creation")
    void shouldThrowExceptionIfCarNotFoundOnCreate() {
        when(buyerRepository.findById(testBuyer.getId())).thenReturn(Optional.of(testBuyer));
        when(carRepository.findById(anyInt())).thenReturn(Optional.empty()); // Car not found

        assertThrows(RuntimeException.class, () -> reviewService.createReview(reviewDto));

        verify(buyerRepository, times(1)).findById(testBuyer.getId());
        verify(carRepository, times(1)).findById(anyInt());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should retrieve review by ID successfully")
    void shouldGetReviewByIdSuccessfully() {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        ReviewDto foundDto = reviewService.getReviewById(1);

        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(1);
        assertThat(foundDto.getRate()).isEqualTo("5");
        assertThat(foundDto.getFeedback()).isEqualTo("Excellent car, very comfortable and reliable!");
        assertThat(foundDto.getBuyerId()).isEqualTo(testBuyer.getId());
        assertThat(foundDto.getCarId()).isEqualTo(testCar.getId());

        verify(reviewRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw RuntimeException when review not found by ID")
    void shouldThrowExceptionWhenReviewNotFoundById() {
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reviewService.getReviewById(99));
        verify(reviewRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Should retrieve all reviews")
    void shouldGetAllReviews() {
        // Create another review for testing getAll
        Review review2 = new Review();
        review2.setId(2);
        review2.setRate("3");
        review2.setFeedback("Good car, but fuel economy could be better.");
        review2.setBuyer(testBuyer);
        review2.setCar(testCar);

        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review, review2));

        List<ReviewDto> reviewDtos = reviewService.getAllReviews();

        assertThat(reviewDtos).isNotNull();
        assertThat(reviewDtos).hasSize(2);
        assertThat(reviewDtos).extracting(ReviewDto::getRate).containsExactlyInAnyOrder("5", "3");
        assertThat(reviewDtos).extracting(ReviewDto::getFeedback)
                .containsExactlyInAnyOrder("Excellent car, very comfortable and reliable!", "Good car, but fuel economy could be better.");

        verify(reviewRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent review")
    void shouldThrowExceptionWhenUpdatingNonExistentReview() {
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty()); // Review not found

        assertThrows(RuntimeException.class, () -> reviewService.updateReview(99, reviewDto));

        verify(reviewRepository, times(1)).findById(99);
        verify(buyerRepository, never()).findById(anyInt()); // Should not look up buyer/car if review isn't found
        verify(carRepository, never()).findById(anyInt());
        verify(reviewRepository, never()).save(any(Review.class));
    }


    @Test
    @DisplayName("Should delete a review successfully")
    void shouldDeleteReviewSuccessfully() {
        when(reviewRepository.existsById(1)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1);

        reviewService.deleteReview(1);

        verify(reviewRepository, times(1)).existsById(1);
        verify(reviewRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent review")
    void shouldThrowExceptionWhenDeletingNonExistentReview() {
        when(reviewRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(99));

        verify(reviewRepository, times(1)).existsById(99);
        verify(reviewRepository, never()).deleteById(anyInt()); // Should not attempt delete
    }

    // You might also add tests for methods like getReviewsByCarId, getReviewsByBuyerId if they exist in your service
    // @Test
    // @DisplayName("Should retrieve reviews by car ID")
    // void shouldGetReviewsByCarId() {
    //    // Setup more reviews, some for testCar, some for anotherCar
    //    // Mock reviewRepository.findByCarId(testCar.getId())
    //    // Assert results
    // }
}