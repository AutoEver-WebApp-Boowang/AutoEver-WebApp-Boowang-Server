package com.example.boowang.global.security.token;

import com.example.boowang.global.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

// Refresh Token 쿠키의 공통 설정을 관리한다.
@Component
@RequiredArgsConstructor
public class RefreshTokenCookieProvider {

    public static final String COOKIE_NAME = "refreshToken";
    private static final String COOKIE_PATH = "/api/v1/auth";

    private final JwtProperties jwtProperties;

    @Value("${app.auth.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.auth.cookie-same-site}")
    private String cookieSameSite;

    // 로그인과 재발급 성공 시 사용할 Refresh Token 쿠키를 만든다.
    public ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from(COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(COOKIE_PATH)
                .maxAge(Duration.ofMillis(
                        jwtProperties.getRefreshTokenExpirationMs()
                ))
                .build();
    }

    // 로그아웃 시 브라우저에서 Refresh Token 쿠키를 삭제한다.
    public ResponseCookie deleteCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}