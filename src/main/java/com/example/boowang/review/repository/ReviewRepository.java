package com.example.boowang.review.repository;

import com.example.boowang.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByPlace_Id(Long placeId, Pageable pageable);

    Optional<Review> findTopByPlace_IdOrderByCreatedAtDesc(Long placeId);  // lastconfirmedAt 부분

    long countByPlace_Id(Long placeId);  // reviewcount 부분
}