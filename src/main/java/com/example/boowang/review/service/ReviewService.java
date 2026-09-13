package com.example.boowang.review.service;

import com.example.boowang.review.entity.Review;
import com.example.boowang.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을수가 없습니다. "));
    }

    public Review create(Review review) {
        return reviewRepository.save(review);
    }

    public Review update(Long id, Review request){
        Review review = findById(id);
        review.setContent(request.getContent());
        return reviewRepository.save(review);
    }

    public void delete(Long id) {

        reviewRepository.deleteById(id);
    }
}
