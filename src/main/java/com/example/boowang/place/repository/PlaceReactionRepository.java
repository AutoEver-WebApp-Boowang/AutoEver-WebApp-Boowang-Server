package com.example.boowang.place.repository;

import com.example.boowang.place.entity.PlaceReaction;
import com.example.boowang.place.entity.PlaceReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceReactionRepository extends JpaRepository<PlaceReaction, PlaceReactionId> {

    long countByPlaceIdAndReactionType(Long placeId, String reactionType);
}
