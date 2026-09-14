package com.example.boowang.auth.repository;

import com.example.boowang.auth.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthSessionRepository
        extends JpaRepository<AuthSession, Long> {

    // 로그아웃하지 않은 Refresh Token 세션을 찾는다.
    Optional<AuthSession> findByRefreshTokenHashAndRevokedAtIsNull(
            String refreshTokenHash
    );

    // 한 사용자의 로그아웃하지 않은 모든 세션을 찾는다.
    List<AuthSession> findAllByUser_IdAndRevokedAtIsNull(
            Long userId
    );
}