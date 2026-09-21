package com.example.boowang.place.dto.request;

import jakarta.validation.constraints.Pattern;

// 장소 추천/비추천 등록·변경 요청
public record PlaceReactionRequest(
        @Pattern(regexp = "^(추천|비추천)$")
        String reactionType
) {
}