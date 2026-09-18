package com.example.boowang.review.controller;

import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.place.entity.Place;
import com.example.boowang.review.dto.request.ReviewCreateRequest;
import com.example.boowang.review.dto.response.ReviewCreateResponse;
import com.example.boowang.review.dto.response.ReviewListResponse;
import com.example.boowang.review.entity.Review;
import com.example.boowang.review.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places/{placeId}/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {
    private final ReviewService reviewService;


    //리뷰 목록 조회
    @GetMapping
    public ApiResponse<ReviewListResponse> getReviews(@PathVariable Long placeId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){
        return ApiResponse.success(reviewService.findByPlace(placeId, page, size));
    }


    //리뷰 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewCreateResponse> createReview(@PathVariable Long placeId, @RequestBody ReviewCreateRequest request) {
        return ApiResponse.success(reviewService.create(placeId, request));
    }



}
