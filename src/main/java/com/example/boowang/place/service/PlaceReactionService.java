package com.example.boowang.place.service;

import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.place.dto.request.PlaceReactionRequest;
import com.example.boowang.place.entity.Place;
import com.example.boowang.place.entity.PlaceReaction;
import com.example.boowang.place.repository.PlaceReactionRepository;
import com.example.boowang.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceReactionService {

    private final PlaceRepository placeRepository;
    private final PlaceReactionRepository placeReactionRepository;

    // POST /api/places/{placeId}/reactions -- 추천/비추천 등록
    public void reactToPlace(Long userId, Long placeId, PlaceReactionRequest request) {
        Place place = placeRepository.findById(placeId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        placeReactionRepository.findByUserIdAndPlaceId(userId, place.getId())
                .ifPresentOrElse(
                        reaction -> reaction.changeReactionType(request.reactionType()),
                        () -> placeReactionRepository.save(
                                PlaceReaction.builder()
                                        .userId(userId)
                                        .placeId(place.getId())
                                        .reactionType(request.reactionType())
                                        .build()
                        )
                );
    }

    // DELETE /api/places/{placeId}/reactions -- 추천/비추천 취소
    public void cancelReaction(Long userId, Long placeId) {
        placeReactionRepository.deleteByUserIdAndPlaceId(userId, placeId);
    }
}