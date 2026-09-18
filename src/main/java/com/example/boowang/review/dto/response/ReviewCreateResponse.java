package com.example.boowang.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewCreateResponse {
    private Long id;
    private LocalDateTime createdAt;
}