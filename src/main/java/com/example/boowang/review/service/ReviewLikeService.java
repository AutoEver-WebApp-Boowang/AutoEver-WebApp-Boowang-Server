package com.example.boowang.review.service;


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

    public ReviewLike like(Long reviewId, Long userId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if(reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다.");
        }

        ReviewLike like = new ReviewLike();
        like.setReviewId(reviewId);
        like.setUserId(userId);
        return reviewLikeRepository.save(like);
    }
    @Transactional
    public void unlike(Long reviewId, Long userId) {
        if(!reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "좋아요를 누르지 않았습니다.");
        }
        reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);
    }
}
