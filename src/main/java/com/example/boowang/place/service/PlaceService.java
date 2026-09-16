package com.example.boowang.place.service;

import com.example.boowang.place.dto.request.PlaceRegisterRequest;
import com.example.boowang.place.dto.response.PlaceDetailResponse;
import com.example.boowang.place.dto.response.PlaceListResponse;
import com.example.boowang.place.dto.response.PlaceSearchResponse;
import com.example.boowang.place.dto.response.PlaceSummaryResponse;
import com.example.boowang.place.entity.ParkingDetail;
import com.example.boowang.place.entity.Place;
import com.example.boowang.place.entity.PlacePhoto;
import com.example.boowang.place.repository.*;
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

    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;
    private final ParkingDetailRepository parkingDetailRepository;
    private final PlaceReactionRepository placeReactionRepository;
    private final PlacePhotoRepository placePhotoRepository;

    // GET /api/places  -- 장소 전체 조회
    public PlaceListResponse getNearbyPlaces(BigDecimal lat, BigDecimal lng, Integer precision,
                                             Boolean isFree, Boolean hasRoof) {
        int digits = (precision != null) ? precision : 3;
        BigDecimal unit = BigDecimal.ONE.movePointLeft(digits);

        BigDecimal latFloor = lat.setScale(digits, RoundingMode.FLOOR);
        BigDecimal lngFloor = lng.setScale(digits, RoundingMode.FLOOR);
        BigDecimal latCeil = latFloor.add(unit);
        BigDecimal lngCeil = lngFloor.add(unit);

        List<Place> places = placeRepository.findByCoordinateRange(
                latFloor, latCeil, lngFloor, lngCeil, isFree, hasRoof
        );

        List<PlaceSummaryResponse> result = places.stream()
                .map(this::toSummary)
                .toList();

        return new PlaceListResponse(result);
    }

    // GET /api/places/search   -- 장소 검색
    public PlaceSearchResponse searchPlaces(String keyword) {
        List<Place> found = placeRepository.searchByKeyword(keyword);

        List<PlaceSummaryResponse> result = found.stream()
                .map(this::toSummary)
                .toList();

        return new PlaceSearchResponse(result, result.size());
    }

    // GET /api/places/{placeId}  -- 장소 상세정보 조회
    public PlaceDetailResponse getPlaceDetail(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다. placeId=" + placeId));

        ParkingDetail pd = place.getParkingDetail();

        long recommendCount = placeReactionRepository.countByPlaceIdAndReactionType(placeId, "추천");
        long notRecommendCount = placeReactionRepository.countByPlaceIdAndReactionType(placeId, "비추천");

        List<String> photos = placePhotoRepository.findByPlaceIdOrderBySortOrderAsc(placeId)
                .stream()
                .map(PlacePhoto::getImageUrl)
                .toList();

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
                photos
        );
    }

    // POST /api/places — 장소 등록
    @Transactional
    public Long registerPlace(Long userId, PlaceRegisterRequest request) {
        Place place = Place.builder()
                .createdBy(userId)
                .name(request.name())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .description(request.description())
                .type(request.type())
                .build();
        placeRepository.save(place);

        ParkingDetail parkingDetail = ParkingDetail.builder()
                .place(place)
                .isFree(request.isFree())
                .hasRoof(request.hasRoof())
                .feeDescription(request.feeDescription())
                .capacity(request.capacity())
                .operatingHours(request.operatingHours())
                .build();
        parkingDetailRepository.save(parkingDetail);

        return place.getId();
    }



    // PATCH /api/places/{placeId}



    // DELETE /api/places/{placeId}


    // POST /api/places/{placeId}/favorites


    // DELETE /api/places/{placeId}/favorites


    // GET /api/users/me/favorites



    private PlaceSummaryResponse toSummary(Place place) {
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
}