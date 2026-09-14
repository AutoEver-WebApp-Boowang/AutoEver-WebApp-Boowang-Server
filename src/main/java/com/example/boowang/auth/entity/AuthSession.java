package com.example.boowang.auth.entity;

import com.example.boowang.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// 한 브라우저의 로그인 상태를 저장하는 엔티티
// 예를 들어 크롬과 엣지에서 각각 로그인하면 AuthSession 데이터가 2개 만들어질 수 있다.
@Entity
@Table(name = "auth_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthSession {

    // 로그인 세션 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 로그인 세션을 사용하고 있는 회원
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Refresh Token 원본은 DB에 저장하지 않는다.
    // SHA-256으로 변환한 64글자 해시값만 저장한다.
    @Column(
            name = "refresh_token_hash",
            nullable = false,
            length = 64,
            columnDefinition = "CHAR(64)"
    )
    private String refreshTokenHash;

    // Refresh Token이 만료되는 시간
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 로그아웃한 시간
    // 아직 사용 중인 세션이면 null이다.
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    // 로그인 세션이 처음 만들어진 시간
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 외부에서 new AuthSession...을 직접 사용하지 못하게 한다.
    private AuthSession(
            User user,
            String refreshTokenHash,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.refreshTokenHash = refreshTokenHash;
        this.expiresAt = expiresAt;
    }

    // 새로운 로그인 세션을 만들 때 사용한다.
    public static AuthSession create(
            User user,
            String refreshTokenHash,
            LocalDateTime expiresAt
    ) {
        return new AuthSession(user, refreshTokenHash, expiresAt);
    }

    // 현재 브라우저에서 로그아웃하면 폐기 시간을 기록한다.
    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }

    // 이미 로그아웃한 세션인지 확인한다.
    public boolean isRevoked() {
        return revokedAt != null;
    }

    // Refresh Token의 사용 시간이 끝났는지 확인한다.
    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now());
    }
}