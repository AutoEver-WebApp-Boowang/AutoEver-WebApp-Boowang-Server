package com.example.boowang.place.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlaceDetailResponse(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isFree,
        Boolean hasRoof,
        String operatingHours, // 운영시간
        Integer capacity,  // 주차 가능 대수
        String feeDescription,  // 유료 요금
        String description,
        Integer recommendCount,
        Integer reviewCount,
        LocalDateTime updatedAt


) {
}
