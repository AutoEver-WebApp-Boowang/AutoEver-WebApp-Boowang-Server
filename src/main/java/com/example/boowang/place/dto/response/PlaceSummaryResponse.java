package com.example.boowang.place.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlaceSummaryResponse(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isFree,
        Boolean hasRoof,
        String operatingHours, // 운영시간
        String thumbnailUrl, // 대표 이미지
        String type, // 공영/제보 구분
        LocalDateTime lastConfirmedAt, // 최근 확인 시각
        Integer recommendCount, // 추천 수
        Integer notRecommendCount // 비추천 수
        // Double distance  -> 추후에 구현
) {
}