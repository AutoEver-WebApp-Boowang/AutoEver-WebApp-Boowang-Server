package com.example.boowang.place.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "place_photos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlacePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "image_url", nullable = false, length = 2048)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PlacePhoto(Long placeId, Long uploadedBy, String imageUrl, Integer sortOrder) {
        this.placeId = placeId;
        this.uploadedBy = uploadedBy;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
