package com.example.boowang.review.controller;

import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.review.entity.ReviewLike;
import com.example.boowang.review.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}/likes")
    public ApiResponse<ReviewLike> like(@PathVariable Long reviewId, @RequestParam Long userId) {
        return ApiResponse.success(reviewLikeService.like(reviewId, userId));
    }
    @DeleteMapping("/{reviewId}/like")
    public ApiResponse<Void> unlike(
            @PathVariable Long reviewId,
            @RequestParam Long userId
    ) {
        reviewLikeService.unlike(reviewId, userId);
        return ApiResponse.success(null);
    }

}
