package com.example.boowang.review.controller;

import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.AuthenticatedUser;
import com.example.boowang.review.dto.response.ReviewLikeResponse;
import com.example.boowang.review.entity.ReviewLike;
import com.example.boowang.review.service.ReviewLikeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}/likes")
    public ApiResponse<ReviewLikeResponse> like(@PathVariable Long reviewId, @AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.success(reviewLikeService.like(reviewId, user.getUserId()));
    }
    @DeleteMapping("/{reviewId}/likes")
    public ApiResponse<Void> unlike(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        reviewLikeService.unlike(reviewId, user.getUserId());
        return ApiResponse.success(null);
    }

}
