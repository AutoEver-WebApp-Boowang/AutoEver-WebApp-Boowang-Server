package com.example.boowang.review.service;

import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.review.dto.request.ReviewCreateRequest;
import com.example.boowang.review.dto.response.ReviewCreateResponse;
import com.example.boowang.review.dto.response.ReviewListResponse;
import com.example.boowang.review.dto.response.ReviewResponse;
import com.example.boowang.review.entity.Review;
import com.example.boowang.review.repository.ReviewLikeRepository;
import com.example.boowang.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;


    //리뷰 목록 조회
    public ReviewListResponse findByPlace(Long placeId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findByPlaceId(placeId, pageable);

        List<ReviewResponse> reviews = reviewPage.getContent().stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getContent(),
                        reviewLikeRepository.countByReviewId(review.getId()),
                        review.getCreatedAt()
                ))
                .toList();
        return new ReviewListResponse(reviews, reviewPage.getTotalElements());
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }

    //리뷰 작성
    public ReviewCreateResponse create(Long placeId, ReviewCreateRequest request) {
        Review review = new Review();
        review.setPlaceId(placeId);
        review.setUserId(1L);
        review.setContent(request.getContent());
        Review saved = reviewRepository.save(review);
        return new ReviewCreateResponse(saved.getId(), saved.getCreatedAt());

    }



}
