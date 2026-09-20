package com.example.boowang.review.service;


import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.review.dto.response.ReviewLikeResponse;
import com.example.boowang.review.entity.ReviewLike;
import com.example.boowang.review.repository.ReviewLikeRepository;
import com.example.boowang.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;

    // 리뷰 좋아요
    public ReviewLikeResponse like(Long reviewId, Long userId) {


        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        if(reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_LIKED);
        }

        ReviewLike like = new ReviewLike();
        like.setReview(reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND)));
        like.setUserId(userId);

        reviewLikeRepository.save(like);
        long likeCount = reviewLikeRepository.countByReviewId(reviewId);
        return new ReviewLikeResponse(reviewId, likeCount);
    }
    @Transactional
    public void unlike(Long reviewId, Long userId) {
        if(!reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new BusinessException(ErrorCode.REVIEW_LIKE_NOT_FOUND);
        }

        reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);
    }
}
