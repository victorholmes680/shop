package com.shopmall.repository;

import com.shopmall.entity.Product;
import com.shopmall.entity.Review;
import com.shopmall.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProduct(Product product, Pageable pageable);

    Optional<Review> findByUserAndProduct(User user, Product product);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double getAverageRatingByProduct(@Param("product") Product product);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product")
    Long countByProduct(@Param("product") Product product);

    List<Review> findTop5ByProductOrderByCreatedAtDesc(Product product);

    @Query("SELECT r FROM Review r WHERE r.verifiedPurchase = true ORDER BY r.createdAt DESC")
    Page<Review> findVerifiedReviews(Pageable pageable);

    Page<Review> findByUser(User user, Pageable pageable);
}
