package com.example.boowang.place.service;

import com.example.boowang.place.dto.response.PlaceListResponse;
import com.example.boowang.place.dto.response.PlaceSearchResponse;
import com.example.boowang.place.dto.response.PlaceSummaryResponse;
import com.example.boowang.place.entity.ParkingDetail;
import com.example.boowang.place.entity.Place;
import com.example.boowang.place.repository.FavoriteRepository;
import com.example.boowang.place.repository.ParkingDetailRepository;
import com.example.boowang.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;
    private final ParkingDetailRepository parkingDetailRepository;

    // GET /api/places
    public PlaceListResponse getPlaces(Boolean isFree, Boolean hasRoof) {
        List<Place> places = placeRepository.findAllWithFilter(isFree, hasRoof);

        List<PlaceSummaryResponse> result = places.stream()
                .map(this::toSummary)
                .toList();

        return new PlaceListResponse(result);
    }

    // GET /api/places/search
    public PlaceSearchResponse searchPlaces(String keyword) {
        List<Place> found = placeRepository.searchByKeyword(keyword);

        List<PlaceSummaryResponse> result = found.stream()
                .map(this::toSummary)
                .toList();

        return new PlaceSearchResponse(result, result.size());
    }

    private PlaceSummaryResponse toSummary(Place place) {
        ParkingDetail pd = place.getParkingDetail();

        return new PlaceSummaryResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                pd != null ? pd.getIsFree() : null,
                pd != null ? pd.getHasRoof() : null,
                pd != null ? pd.getOperatingHours() : null
        );
    }

    // POST /api/places



    // PATCH /api/places/{placeId}



    // DELETE /api/places/{placeId}


    // POST /api/places/{placeId}/favorites


    // DELETE /api/places/{placeId}/favorites


    // GET /api/users/me/favorites
}
