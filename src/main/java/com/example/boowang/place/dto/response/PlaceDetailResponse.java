package com.example.boowang.place.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PlaceDetailResponse(
        Long id,
        String name,
        String address,
        String detailAddress,  // 상세주소
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isFree,
        Boolean hasRoof,
        String operatingHours, // 운영시간
        Integer capacity,  // 주차 가능 대수
        String feeDescription,  // 유료 요금
        String description,
        String type, // 공영/제보 구분
        LocalDateTime lastConfirmedAt, // 최근 확인 시각
        Integer recommendCount,
        Integer notRecommendCount, // 비추천 수
        Integer reviewCount,
        LocalDateTime updatedAt,
        List<String> photos,  // 이미지 리스트
        String myReaction  // 추천/비추천/안누름
//        Boolean isFavorited  // true/false
) {
}