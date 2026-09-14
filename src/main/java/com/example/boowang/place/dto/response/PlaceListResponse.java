package com.example.boowang.place.dto.response;

import java.util.List;

public record PlaceListResponse(
        List<PlaceSummaryResponse> places
) {
}
