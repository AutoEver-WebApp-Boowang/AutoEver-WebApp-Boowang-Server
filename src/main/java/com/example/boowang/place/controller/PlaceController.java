package com.example.boowang.place.controller;

import com.example.boowang.place.dto.response.PlaceListResponse;
import com.example.boowang.place.dto.response.PlaceSearchResponse;
import com.example.boowang.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;


    // GET /api/places
    @GetMapping
    public PlaceListResponse getPlaces(
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) Boolean hasRoof
    ) {
        return placeService.getPlaces(isFree, hasRoof);
    }

    // GET /api/places/search
    @GetMapping("/search")
    public PlaceSearchResponse searchPlaces(@RequestParam String keyword) {
        return placeService.searchPlaces(keyword);
    }



    // POST /api/places



    // PATCH /api/places/{placeId}



    // DELETE /api/places/{placeId}


    // POST /api/places/{placeId}/favorites


    // DELETE /api/places/{placeId}/favorites


    // GET /api/users/me/favorites
}
