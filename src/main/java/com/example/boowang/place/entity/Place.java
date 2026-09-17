package com.example.boowang.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 7)
    private BigDecimal longitude;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 공영/제보
    @Column(length = 10)
    private String type;

    // 최근정보 확인 시간
    @Column(name = "last_confirmed_at")
    private LocalDateTime lastConfirmedAt;

    // ParkingDetail 쪽 @OneToOne의 주인은 ParkingDetail 이니까 mappedBy로 연결만
    @OneToOne(mappedBy = "place", fetch = FetchType.LAZY)
    private ParkingDetail parkingDetail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Place(Long createdBy, String name, String address, String detailAddress,
                 BigDecimal latitude, BigDecimal longitude, String description, String type) {
        this.createdBy = createdBy;
        this.name = name;
        this.address = address;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.type = type;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 추가: 소프트 삭제 처리
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 추가: 최근 확인 시각 갱신
    public void confirmNow() {
        this.lastConfirmedAt = LocalDateTime.now();
    }
}