package com.example.boowang.auth.dto;

import com.example.boowang.auth.dto.response.AccessTokenResponse;
import lombok.Getter;

//서비스에서 어제만든 성공처리로 전달할 로그인 결과
@Getter
public class SocialLoginResult {
    //JSON 응답으로 전달할 액세스 토큰 정보
    private final AccessTokenResponse accessTokenResponse;

    //HttpOnly 쿠키에 넣을 리프레시 토큰 원본
    private final String refreshToken;

    public SocialLoginResult(
            AccessTokenResponse accessTokenResponse,
            String refreshToken
    ) {
        this.accessTokenResponse = accessTokenResponse;
        this.refreshToken = refreshToken;
    }
}
