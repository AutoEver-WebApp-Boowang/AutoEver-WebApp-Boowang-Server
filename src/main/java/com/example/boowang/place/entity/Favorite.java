package com.example.boowang.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(FavoriteId.class) // @IdClass -> 복합키 사용
public class Favorite {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "place_id")
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", insertable = false, updatable = false)
    private Place place;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Favorite(Long userId, Long placeId) {
        this.userId = userId;
        this.placeId = placeId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
