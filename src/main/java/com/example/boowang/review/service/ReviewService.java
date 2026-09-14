package com.example.boowang.review.service;


import com.example.boowang.review.entity.Review;
import com.example.boowang.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    //리뷰 작성메서드
    public Review createReview(Long placeId, Review review) {
        review.setPlaceId(placeId);
        return reviewRepository.save(review);
    }

    //리뷰 목록 조회(페이지로)


    public Page<Review> getReviews(Long placeId, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return reviewRepository.findByPlaceId(placeId, pageable);
    }
}
