package com.example.boowang.review.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String content;
    private Long likeCount;
    private LocalDateTime createdAt;
    private String nickname;
    private boolean isLike;
}
