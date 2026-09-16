package com.example.boowang.place.controller;

import com.example.boowang.place.service.PlacePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/places/{placeId}/photos")
@RequiredArgsConstructor
public class PlacePhotoController {

    private final PlacePhotoService placePhotoService;

    // POST /api/places/{placeId}/photos  -- 장소 이미지 업로드 (장소 제보할때)
    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadPhoto(
            @PathVariable Long placeId,
            @RequestParam Long uploadedBy,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Integer sortOrder
    ) {
        String imageUrl = placePhotoService.uploadPhoto(placeId, uploadedBy, file, sortOrder);
        return Map.of("imageUrl", imageUrl);
    }
}