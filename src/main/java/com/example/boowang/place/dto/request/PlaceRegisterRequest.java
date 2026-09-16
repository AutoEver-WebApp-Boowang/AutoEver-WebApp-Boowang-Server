package com.example.boowang.place.dto.request;

import java.math.BigDecimal;

public record PlaceRegisterRequest(
        String name,
        String address,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String description,
        String type, // 공영제보
        Boolean isFree,
        Boolean hasRoof,
        String feeDescription,
        Integer capacity,
        String operatingHours
) {
}