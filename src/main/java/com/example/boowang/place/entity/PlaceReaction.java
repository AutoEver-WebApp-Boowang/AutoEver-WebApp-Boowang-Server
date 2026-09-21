package com.example.boowang.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "place_reaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PlaceReactionId.class) // @IdClass -> 복합키 사용
public class PlaceReaction {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "reaction_type", nullable = false, length = 10)
    private String reactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public PlaceReaction(Long userId, Long placeId, String reactionType) {
        this.userId = userId;
        this.placeId = placeId;
        this.reactionType = reactionType;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void changeReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

}
