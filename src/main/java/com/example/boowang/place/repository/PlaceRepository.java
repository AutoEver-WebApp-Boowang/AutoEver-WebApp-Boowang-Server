package com.example.boowang.place.repository;

import com.example.boowang.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    // GET /api/places — 위치 + 필터 조회
    @Query("""
        SELECT p FROM Place p
        JOIN p.parkingDetail pd
        WHERE p.deletedAt IS NULL
         /* AND p.latitude BETWEEN :minLat AND :maxLat
          AND p.longitude BETWEEN :minLng AND :maxLng */
          AND (:isFree IS NULL OR pd.isFree = :isFree)
          AND (:hasRoof IS NULL OR pd.hasRoof = :hasRoof)
        """)
    List<Place> findAllWithFilter(
           /* @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng, */
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