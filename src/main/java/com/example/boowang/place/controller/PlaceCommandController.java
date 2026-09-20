package com.example.boowang.place.controller;

import com.example.boowang.global.security.AuthenticatedUser;
import com.example.boowang.place.dto.request.PlaceRegisterRequest;
import com.example.boowang.place.dto.request.PlaceUpdateRequest;
import com.example.boowang.place.service.PlaceCommandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PlaceCommandController {

    private final PlaceCommandService placeCommandService;

    // POST /api/places -- 장소 등록 (인증 필요)
    @PostMapping("/api/places")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> registerPlace(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody PlaceRegisterRequest request
    ) {
        Long placeId = placeCommandService.registerPlace(user.getUserId(), request);
        return Map.of("id", placeId);
    }

    // PATCH /api/places/{placeId} -- 장소정보 수정 (인증 필요)
    @PatchMapping("/api/places/{placeId}")
    public Map<String, String> updatePlace(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long placeId,
            @RequestBody PlaceUpdateRequest request
    ) {
        placeCommandService.updatePlaceFee(user.getUserId(), placeId, request);
        return Map.of("message", "장소정보가 수정되었습니다.");
    }

    // DELETE /api/places/{placeId} -- 장소정보 삭제 (인증 필요)
    @DeleteMapping("/api/places/{placeId}")
    public Map<String, String> deletePlace(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long placeId
    ) {
        placeCommandService.deletePlace(user.getUserId(), placeId);
        return Map.of("message", "장소가 삭제되었습니다.");
    }

    // POST /api/places/{placeId}/favorites -- 즐겨찾기 추가 (인증 필요)
    @PostMapping("/api/places/{placeId}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> addFavorite(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long placeId
    ) {
        placeCommandService.addFavorite(user.getUserId(), placeId);
        return Map.of("message", "내 즐겨찾기에 추가되었습니다.");
    }

    // DELETE /api/places/{placeId}/favorites -- 즐겨찾기 삭제 (인증 필요)
    @DeleteMapping("/api/places/{placeId}/favorites")
    public Map<String, String> removeFavorite(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long placeId
    ) {
        placeCommandService.removeFavorite(user.getUserId(), placeId);
        return Map.of("message", "즐겨찾기가 삭제되었습니다.");
    }
}