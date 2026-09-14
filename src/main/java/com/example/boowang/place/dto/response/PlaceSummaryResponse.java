package com.example.boowang.place.dto.response;

import java.math.BigDecimal;

public record PlaceSummaryResponse(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isFree,
        Boolean hasRoof,
        String operatingHours // 운영시간
        // Double distance  -> 추후에 구현

) {
}