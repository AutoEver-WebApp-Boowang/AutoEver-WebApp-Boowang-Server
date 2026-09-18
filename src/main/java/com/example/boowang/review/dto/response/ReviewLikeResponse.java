package com.example.boowang.review.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ReviewLikeResponse {
    private Long reviewId;
    private Long likeCount;
}
