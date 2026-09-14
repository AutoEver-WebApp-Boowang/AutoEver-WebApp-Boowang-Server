package com.example.boowang.review.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String content;
    private Long likeCount;
    private LocalDateTime createdAt;
}
