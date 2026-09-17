package com.example.boowang.place.service;

import com.example.boowang.place.dto.request.PlaceRegisterRequest;
import com.example.boowang.place.dto.request.PlaceUpdateRequest;
import com.example.boowang.place.dto.response.PlaceSummaryResponse;
import com.example.boowang.place.entity.Favorite;
import com.example.boowang.place.entity.ParkingDetail;
import com.example.boowang.place.entity.Place;
import com.example.boowang.place.repository.FavoriteRepository;
import com.example.boowang.place.repository.ParkingDetailRepository;
import com.example.boowang.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceCommandService {

    private static final int COORDINATE_SCALE = 6;

    private final PlaceRepository placeRepository;
    private final ParkingDetailRepository parkingDetailRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlaceService placeService;

    // POST /api/places -- 장소 등록
    public Long registerPlace(Long userId, PlaceRegisterRequest request) {
        Place place = Place.builder()
                .createdBy(userId)
                .name(request.name())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .latitude(round(request.latitude()))
                .longitude(round(request.longitude()))
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

    // PATCH /api/places/{placeId} -- 장소정보 수정
    public void updatePlaceFee(Long placeId, PlaceUpdateRequest request) {
        if (request.feeDescription() == null && request.capacity() == null && request.hasRoof() == null) {
            throw new IllegalArgumentException("수정할 정보를 입력해 주세요.");
        }

        ParkingDetail parkingDetail = parkingDetailRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당장소의 주차 정보가 없습니다. placeId=" + placeId));
        parkingDetail.updateFee(request.feeDescription(), request.capacity(), request.hasRoof());

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다. placeId=" + placeId));
        place.confirmNow(); // 최근확인 (수정한시간)
    }

    // DELETE /api/places/{placeId} -- 장소정보 삭제
    public void deletePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다. placeId=" + placeId));
        place.softDelete();
    }

    // POST /api/places/{placeId}/favorites -- 즐겨찾기 추가
    public void addFavorite(Long userId, Long placeId) {
        favoriteRepository.findByUserIdAndPlaceId(userId, placeId)
                .ifPresent(f -> { throw new IllegalStateException("이미 즐겨찾기한 장소입니다."); });

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .placeId(placeId)
                .build();
        favoriteRepository.save(favorite);
    }

    // DELETE /api/places/{placeId}/favorites -- 즐겨찾기 삭제
    public void removeFavorite(Long userId, Long placeId) {
        favoriteRepository.deleteByUserIdAndPlaceId(userId, placeId);
    }

    // GET /api/users/me/favorites -- 즐겨찾기 목록 조회
    @Transactional(readOnly = true)
    public List<PlaceSummaryResponse> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(fav -> placeRepository.findById(fav.getPlaceId()).orElse(null))
                .filter(place -> place != null && place.getDeletedAt() == null)
                .map(placeService::toSummary)
                .toList();
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(COORDINATE_SCALE, RoundingMode.HALF_UP);
    }
}