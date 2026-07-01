package com.sport_pro_be.modules.review.repository;

import com.sport_pro_be.modules.review.domain.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Page<ProductReview> findByProductIdAndIsActiveTrue(Long productId, Pageable pageable);
    List<ProductReview> findByProductIdAndIsActiveTrue(Long productId);
    boolean existsByOrderItemId(Long orderItemId);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM ProductReview r WHERE r.isActive = true")
    Optional<Double> findAverageRating();

    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.isActive = true")
    long countActiveReviews();
}
