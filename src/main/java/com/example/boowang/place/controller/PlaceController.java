package com.example.boowang.place.controller;

import com.example.boowang.place.dto.response.PlaceDetailResponse;
import com.example.boowang.place.dto.response.PlaceListResponse;
import com.example.boowang.place.dto.response.PlaceSearchResponse;
import com.example.boowang.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // GET /api/places -- Bounding Box 내 장소 조회 (인증 불필요)
    @GetMapping
    public PlaceListResponse getPlaces(
            @RequestParam BigDecimal swLat,
            @RequestParam BigDecimal swLng,
            @RequestParam BigDecimal neLat,
            @RequestParam BigDecimal neLng,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) Boolean hasRoof
    ) {
        return placeService.getPlacesInBoundingBox(swLat, swLng, neLat, neLng, isFree, hasRoof);
    }

    // GET /api/places/search -- 장소 검색 (인증 불필요)
    @GetMapping("/search")
    public PlaceSearchResponse searchPlaces(@RequestParam String keyword) {
        return placeService.searchPlaces(keyword);
    }

    // GET /api/places/{placeId} -- 상세조회 (인증 불필요)
    @GetMapping("/{placeId}")
    public PlaceDetailResponse getPlaceDetail(@PathVariable Long placeId) {
        return placeService.getPlaceDetail(placeId);
    }
}