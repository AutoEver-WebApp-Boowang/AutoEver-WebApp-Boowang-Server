package com.example.boowang.place.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlaceReactionId implements Serializable {
    private Long userId;
    private Long placeId;
}