package com.shopmall.service;

import com.shopmall.dto.ProductRatingResponse;
import com.shopmall.dto.ReviewRequest;
import com.shopmall.dto.ReviewResponse;
import com.shopmall.entity.Order;
import com.shopmall.entity.OrderItem;
import com.shopmall.entity.Product;
import com.shopmall.entity.Review;
import com.shopmall.exception.BadRequestException;
import com.shopmall.exception.ResourceNotFoundException;
import com.shopmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * Get reviews for a product with pagination
     */
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return reviewRepository.findByProduct(product, pageable)
                .map(ReviewResponse::fromEntity);
    }

    /**
     * Get top 5 recent reviews for a product
     */
    public List<ReviewResponse> getTopProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return reviewRepository.findTop5ByProductOrderByCreatedAtDesc(product).stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get product rating summary
     */
    public ProductRatingResponse getProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        List<Review> reviews = reviewRepository.findByProduct(product, Pageable.unpaged()).getContent();
        return ProductRatingResponse.fromReviews(reviews);
    }

    /**
     * Get average rating for a product
     */
    public Double getAverageRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return reviewRepository.getAverageRatingByProduct(product);
    }

    /**
     * Get review count for a product
     */
    public Long getReviewCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return reviewRepository.countByProduct(product);
    }

    /**
     * Check if user has purchased the product
     */
    private boolean hasUserPurchasedProduct(Long userId, Long productId) {
        List<Order> orders = orderRepository.findByUserIdWithItems(userId);

        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    /**
     * Check if user has already reviewed the product
     */
    private boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.findByUserAndProduct(
                        userRepository.findById(userId).orElseThrow(),
                        productRepository.findById(productId).orElseThrow())
                .isPresent();
    }

    /**
     * Create a new review
     */
    @Transactional
    public ReviewResponse createReview(String userEmail, ReviewRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // Check if user already reviewed this product
        if (hasUserReviewedProduct(user.getId(), product.getId())) {
            throw new BadRequestException("You have already reviewed this product");
        }

        // Check if product is active
        if (!product.getActive()) {
            throw new BadRequestException("Cannot review inactive product");
        }

        // Check if user purchased the product (optional - can be disabled)
        boolean hasPurchased = hasUserPurchasedProduct(user.getId(), product.getId());

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .verifiedPurchase(hasPurchased)
                .build();

        review = reviewRepository.save(review);

        return ReviewResponse.fromEntity(review);
    }

    /**
     * Update a review
     */
    @Transactional
    public ReviewResponse updateReview(Long reviewId, String userEmail, ReviewRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // Check if user owns the review
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        return ReviewResponse.fromEntity(review);
    }

    /**
     * Delete a review
     */
    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // Check if user owns the review
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    /**
     * Get all reviews by the authenticated user
     */
    public Page<ReviewResponse> getUserReviews(String userEmail, Pageable pageable) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return reviewRepository.findByUser(user, pageable)
                .map(ReviewResponse::fromEntity);
    }

    /**
     * Delete a review (admin only)
     */
    @Transactional
    public void deleteReviewAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        reviewRepository.delete(review);
    }
}
