package com.example.boowang.review.service;

import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.place.entity.Place;
import com.example.boowang.place.repository.PlaceRepository;
import com.example.boowang.review.dto.request.ReviewCreateRequest;
import com.example.boowang.review.dto.response.ReviewCreateResponse;
import com.example.boowang.review.dto.response.ReviewListResponse;
import com.example.boowang.review.dto.response.ReviewResponse;
import com.example.boowang.review.entity.Review;
import com.example.boowang.review.repository.ReviewLikeRepository;
import com.example.boowang.review.repository.ReviewRepository;
import com.example.boowang.user.entity.User;
import com.example.boowang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;


    //리뷰 목록 조회
    public ReviewListResponse findByPlace(Long placeId, int page, int size){
        placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findByPlace_Id(placeId, pageable);

        List<ReviewResponse> reviews = reviewPage.getContent().stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getContent(),
                        reviewLikeRepository.countByReviewId(review.getId()),
                        review.getCreatedAt(),
                        userRepository.findByIdAndDeletedAtIsNull(review.getUserId())
                                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND))
                                .getNickname()
                ))
                .toList();
        return new ReviewListResponse(reviews, reviewPage.getTotalElements());
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }

    //리뷰 작성
    @Transactional
    public ReviewCreateResponse create(Long placeId,  ReviewCreateRequest request, Long userId) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
        Review review = new Review();
        review.setPlace(place);
        review.setUserId(userId);
        review.setContent(request.getContent());
        Review saved = reviewRepository.save(review);

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.increaseTrustScore();

        return new ReviewCreateResponse(saved.getId(), saved.getCreatedAt());

    }



}
