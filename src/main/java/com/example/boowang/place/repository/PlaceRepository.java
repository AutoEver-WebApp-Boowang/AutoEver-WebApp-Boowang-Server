package com.example.boowang.place.repository;

import com.example.boowang.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    // GET /api/places — Bounding Box 내 장소 조회
    @Query("""
        SELECT p FROM Place p
        JOIN p.parkingDetail pd
        WHERE p.deletedAt IS NULL
          AND p.latitude BETWEEN :swLat AND :neLat
          AND p.longitude BETWEEN :swLng AND :neLng
          AND (:isFree IS NULL OR pd.isFree = :isFree)
          AND (:hasRoof IS NULL OR pd.hasRoof = :hasRoof)
        """)
    List<Place> findByBoundingBox(
            @Param("swLat") BigDecimal swLat,
            @Param("neLat") BigDecimal neLat,
            @Param("swLng") BigDecimal swLng,
            @Param("neLng") BigDecimal neLng,
            @Param("isFree") Boolean isFree,
            @Param("hasRoof") Boolean hasRoof
    );

    // GET /api/places/search — 키워드 검색
    @Query("""
        SELECT p FROM Place p
        WHERE p.deletedAt IS NULL
          AND (p.name LIKE %:keyword% OR p.address LIKE %:keyword%)
        """)
    List<Place> searchByKeyword(@Param("keyword") String keyword);
}