package com.example.boowang.auth.dto.response;

import lombok.Getter;

// 로그인 또는 토큰 재발급 결과로 액세스 토큰 정보를 전달한다.
@Getter
public class AccessTokenResponse {

    // 우리 백엔드에서 발급한 JWT 액세스 토큰
    private final String accessToken;

    // Authorization 헤더에서 사용하는 인증 방식
    private final String tokenType;

    // 액세스 토큰이 만료될 때까지 남은 시간이며 단위는 초이다.
    private final long expiresIn;

    // 액세스 토큰 응답에 필요한 값을 전달받아 저장한다.
    public AccessTokenResponse(
            String accessToken,
            String tokenType,
            long expiresIn
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }
}