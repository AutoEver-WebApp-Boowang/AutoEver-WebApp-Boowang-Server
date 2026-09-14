package com.example.boowang.place.dto.response;

import java.util.List;

// GET /api/places/search 응답값
public record PlaceSearchResponse(
        List<PlaceSummaryResponse> places,
        int totalCount
){
}
