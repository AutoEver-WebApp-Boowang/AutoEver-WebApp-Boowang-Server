package com.example.boowang.place.controller;

import com.example.boowang.global.security.AuthenticatedUser;
import com.example.boowang.place.dto.request.PlaceReactionRequest;
import com.example.boowang.place.service.PlaceReactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/places/{placeId}/reactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PlaceReactionController {

    private final PlaceReactionService placeReactionService;

    // POST /api/places/{placeId}/reactions -- 추천/비추천 등록 (인증 필요)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> react(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long placeId,
            @Valid @RequestBody PlaceReactionRequest request
    ) {
        placeReactionService.reactToPlace(user.getUserId(), placeId, request);
        return Map.of("message", "반영되었습니다.");
    }

    // DELETE /api/places/{placeId}/reactions -- 추천/비추천 취소 (인증 필요)
    @DeleteMapping
    public Map<String, String> cancel(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long placeId
    ) {
        placeReactionService.cancelReaction(user.getUserId(), placeId);
        return Map.of("message", "추천/비추천이 취소되었습니다.");
    }
}
