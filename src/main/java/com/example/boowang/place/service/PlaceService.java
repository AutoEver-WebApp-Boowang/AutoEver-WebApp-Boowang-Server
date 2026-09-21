package com.example.boowang.place.service;

import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.place.dto.response.PlaceDetailResponse;
import com.example.boowang.place.dto.response.PlaceListResponse;
import com.example.boowang.place.dto.response.PlaceSearchResponse;
import com.example.boowang.place.dto.response.PlaceSummaryResponse;
import com.example.boowang.place.entity.ParkingDetail;
import com.example.boowang.place.entity.Place;
import com.example.boowang.place.entity.PlacePhoto;
import com.example.boowang.place.entity.PlaceReaction;
import com.example.boowang.place.repository.FavoriteRepository;
import com.example.boowang.place.repository.PlacePhotoRepository;
import com.example.boowang.place.repository.PlaceReactionRepository;
import com.example.boowang.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private static final int COORDINATE_SCALE = 6;

    private final PlaceRepository placeRepository;
    private final PlaceReactionRepository placeReactionRepository;
    private final PlacePhotoRepository placePhotoRepository;

    // GET /api/places -- Bounding Box 내 장소 조회
    public PlaceListResponse getPlacesInBoundingBox(BigDecimal swLat, BigDecimal swLng,
                                                    BigDecimal neLat, BigDecimal neLng,
                                                    Boolean isFree, Boolean hasRoof) {
        List<Place> places = placeRepository.findByBoundingBox(
                round(swLat), round(neLat), round(swLng), round(neLng), isFree, hasRoof
        );

        List<PlaceSummaryResponse> result = places.stream()
                .map(this::toSummary)
                .toList();

        return new PlaceListResponse(result);
    }

    // GET /api/places/search -- 장소 검색
    public PlaceSearchResponse searchPlaces(String keyword) {
        List<Place> found = placeRepository.searchByKeyword(keyword);

        List<PlaceSummaryResponse> result = found.stream()
                .map(this::toSummary)
                .toList();

        return new PlaceSearchResponse(result, result.size());
    }

    // GET /api/places/{placeId} -- 장소 상세정보 조회
    public PlaceDetailResponse getPlaceDetail(Long placeId, Long userId) {
        Place place = placeRepository.findById(placeId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        ParkingDetail pd = place.getParkingDetail();

        long recommendCount = placeReactionRepository.countByPlaceIdAndReactionType(placeId, "추천");
        long notRecommendCount = placeReactionRepository.countByPlaceIdAndReactionType(placeId, "비추천");

        List<String> photos = placePhotoRepository.findByPlaceIdOrderBySortOrderAsc(placeId)
                .stream()
                .map(PlacePhoto::getImageUrl)
                .toList();

        String myReaction = (userId != null)
                ? placeReactionRepository.findByUserIdAndPlaceId(userId, placeId)
                .map(PlaceReaction::getReactionType)
                .orElse(null)
                : null;

        return new PlaceDetailResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                pd != null ? pd.getIsFree() : null,
                pd != null ? pd.getHasRoof() : null,
                pd != null ? pd.getOperatingHours() : null,
                pd != null ? pd.getCapacity() : null,
                pd != null ? pd.getFeeDescription() : null,
                place.getDescription(),
                place.getType(),
                place.getLastConfirmedAt(),
                (int) recommendCount,
                (int) notRecommendCount,
                0,
                place.getUpdatedAt(),
                photos,
                myReaction
        );
    }

    PlaceSummaryResponse toSummary(Place place) {
        ParkingDetail pd = place.getParkingDetail();

        String thumbnailUrl = placePhotoRepository.findFirstByPlaceIdOrderBySortOrderAsc(place.getId())
                .map(PlacePhoto::getImageUrl)
                .orElse(null);

        long recommendCount = placeReactionRepository.countByPlaceIdAndReactionType(place.getId(), "추천");
        long notRecommendCount = placeReactionRepository.countByPlaceIdAndReactionType(place.getId(), "비추천");

        return new PlaceSummaryResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                pd != null ? pd.getIsFree() : null,
                pd != null ? pd.getHasRoof() : null,
                pd != null ? pd.getOperatingHours() : null,
                thumbnailUrl,
                place.getType(),
                place.getLastConfirmedAt(),
                (int) recommendCount,
                (int) notRecommendCount
        );
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(COORDINATE_SCALE, RoundingMode.HALF_UP);
    }
}