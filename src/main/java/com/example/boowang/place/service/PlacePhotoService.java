package com.example.boowang.place.service;

import com.example.boowang.global.util.S3Uploader;
import com.example.boowang.place.entity.PlacePhoto;
import com.example.boowang.place.repository.PlacePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PlacePhotoService {

    private final S3Uploader s3Uploader;
    private final PlacePhotoRepository placePhotoRepository;

    // POST /api/places/{placeId}/photos  -- 장소 이미지 업로드 (장소 제보할때)
    @Transactional
    public String uploadPhoto(Long placeId, Long uploadedBy, MultipartFile file, Integer sortOrder) {
        String imageUrl = s3Uploader.upload(file);

        PlacePhoto photo = PlacePhoto.builder()
                .placeId(placeId)
                .uploadedBy(uploadedBy)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .build();
        placePhotoRepository.save(photo);

        return imageUrl;
    }
}