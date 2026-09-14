package com.example.boowang.place.repository;

import com.example.boowang.place.entity.Favorite;
import com.example.boowang.place.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndPlaceId(Long userId, Long placeId);
    void deleteByUserIdAndPlaceId(Long userId, Long placeId);


}
