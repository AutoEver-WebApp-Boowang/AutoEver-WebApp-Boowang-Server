package com.example.boowang.place.controller;

import com.example.boowang.global.security.AuthenticatedUser;
import com.example.boowang.place.service.PlacePhotoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/places/{placeId}/photos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PlacePhotoController {

    private final PlacePhotoService placePhotoService;

    // POST /api/places/{placeId}/photos  -- 장소 이미지 업로드 (장소 제보할때)
    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadPhoto(
            @PathVariable Long placeId,
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Integer sortOrder
    ) {
        String imageUrl = placePhotoService.uploadPhoto(placeId, user.getUserId(), file, sortOrder);
        return Map.of("imageUrl", imageUrl);
    }
}