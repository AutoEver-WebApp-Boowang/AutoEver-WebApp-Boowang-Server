package com.example.boowang.auth.entity;

import com.example.boowang.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// 카카오·현대차 계정과 우리 User를 연결하는 테이블
@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints = {
                //같은 sns사용자가 두 번 가입되는 것을 db에서도 막아준다.
                @UniqueConstraint(
                        name = "uk_social_accounts_provider_user",
                        columnNames = {"provider", "provider_user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 소셜 계정으로 가입한 우리 서비스 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // KAKAO, HYUNDAI 중 하나를 문자열로 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider;

    // 카카오 등이 전달하는 사용자의 고유 식별번호
    @Column(name = "provider_user_id", nullable = false, length = 191)
    private String providerUserId;

    // 소셜 계정이 우리 DB에 처음 연결된 시간
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private SocialAccount(
            User user,
            SocialProvider provider,
            String providerUserId
    ) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    // 최초 소셜 로그인 시 SocialAccount를 만드는 메서드
    public static SocialAccount create(
            User user,
            SocialProvider provider,
            String providerUserId
    ) {
        return new SocialAccount(user, provider, providerUserId);
    }
}
