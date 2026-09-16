package com.example.boowang.place.repository;

import com.example.boowang.place.entity.PlacePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlacePhotoRepository extends JpaRepository<PlacePhoto, Long> {

    // 전체조회에서 썸네일이미지
    Optional<PlacePhoto> findFirstByPlaceIdOrderBySortOrderAsc(Long placeId);

    // 상세조회에서 전체 이미지 리스트
    List<PlacePhoto> findByPlaceIdOrderBySortOrderAsc(Long placeId);
}

