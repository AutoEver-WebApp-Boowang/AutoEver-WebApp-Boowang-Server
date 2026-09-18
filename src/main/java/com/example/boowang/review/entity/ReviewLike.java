package com.example.boowang.review.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "review_likes")
@Data
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private  Long userId;


}
