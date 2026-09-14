package com.example.boowang.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParkingDetail {

    @Id
    @Column(name = "place_id")
    private Long placeId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "is_free", nullable = false)
    private Boolean isFree;

    @Column(name = "has_roof", nullable = false)
    private Boolean hasRoof;

    @Column(name = "fee_description", length = 500)
    private String feeDescription;

    private Integer capacity;

    @Column(name = "operating_hours", length = 255)
    private String operatingHours;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ParkingDetail(Place place, Boolean isFree, Boolean hasRoof,
                         String feeDescription, Integer capacity, String operatingHours) {
        this.place = place;
        this.isFree = isFree;
        this.hasRoof = hasRoof;
        this.feeDescription = feeDescription;
        this.capacity = capacity;
        this.operatingHours = operatingHours;
    }

    public void updateFee(String feeDescription, Integer capacity) {
        if (feeDescription != null) this.feeDescription = feeDescription;
        if (capacity != null) this.capacity = capacity;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

