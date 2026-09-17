package com.example.boowang.place.dto.request;

public record PlaceUpdateRequest(
        String feeDescription,
        Integer capacity,
        Boolean hasRoof
) {
}