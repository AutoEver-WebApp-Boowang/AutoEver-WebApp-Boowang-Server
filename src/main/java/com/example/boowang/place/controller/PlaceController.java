package com.example.boowang.place.controller;

import com.example.boowang.place.dto.request.PlaceRegisterRequest;
import com.example.boowang.place.dto.response.PlaceDetailResponse;
import com.example.boowang.place.dto.response.PlaceListResponse;
import com.example.boowang.place.dto.response.PlaceSearchResponse;
import com.example.boowang.place.entity.Favorite;
import com.example.boowang.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // GET /api/places
    @GetMapping
    public PlaceListResponse getPlaces(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(required = false) Integer precision,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) Boolean hasRoof
    ) {
        return placeService.getNearbyPlaces(lat, lng, precision, isFree, hasRoof);
    }

    // GET /api/places/search
    @GetMapping("/search")
    public PlaceSearchResponse searchPlaces(@RequestParam String keyword) {
        return placeService.searchPlaces(keyword);
    }

    // GET /api/places/{placeId}
    @GetMapping("/{placeId}")
    public PlaceDetailResponse getPlaceDetail(@PathVariable Long placeId) {
        return placeService.getPlaceDetail(placeId);
    }

    // POST /api/places
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> registerPlace(
            @RequestParam Long userId,
            @RequestBody PlaceRegisterRequest request
    ) {
        Long placeId = placeService.registerPlace(userId, request);
        return Map.of("id", placeId);
    }



    // PATCH /api/places/{placeId}



    // DELETE /api/places/{placeId}


    // POST /api/places/{placeId}/favorites


    // DELETE /api/places/{placeId}/favorites


    // GET /api/users/me/favorites



}
