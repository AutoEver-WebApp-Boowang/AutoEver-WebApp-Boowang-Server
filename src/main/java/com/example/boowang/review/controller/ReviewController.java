package com.example.boowang.review.controller;


import com.example.boowang.review.entity.Review;
import com.example.boowang.review.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/places/{placeId}/reviews")
public class ReviewController {
    private  final ReviewService reviewService;

    @GetMapping
    public Page<Review> getReviewsList(@RequestParam Long placeId, @RequestParam (defaultValue = "0") int page, @RequestParam (defaultValue = "10") int size){
        return reviewService.getReviews( placeId, page, size);
    }

    @PostMapping
    public Review createReview(@RequestParam Long placeId, @RequestBody Review review){
        return reviewService.createReview(placeId, review);
    }
}
