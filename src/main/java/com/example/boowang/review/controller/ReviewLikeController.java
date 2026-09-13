package com.example.boowang.review.controller;

import com.example.boowang.review.entity.ReviewLike;
import com.example.boowang.review.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}/like")
    public ReviewLike like(@PathVariable Long reviewId, @RequestParam Long userId) {
        return reviewLikeService.like(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like")
    public void unlike(@PathVariable Long reviewId, @RequestParam Long userId) {
        reviewLikeService.unlike(reviewId, userId);
    }

}
