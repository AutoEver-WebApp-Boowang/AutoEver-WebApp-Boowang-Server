package com.example.boowang.auth.controller;

import com.example.boowang.auth.dto.SocialLoginResult;
import com.example.boowang.auth.dto.response.AccessTokenResponse;
import com.example.boowang.auth.service.AuthTokenService;
import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.token.RefreshTokenCookieProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "토큰 재발급")
public class AuthController {

    private final AuthTokenService authTokenService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    // Refresh Token을 검사하고 새 토큰 묶음을 발급한다.
    @PostMapping("/refresh")
    public ApiResponse<AccessTokenResponse> refresh(
            @CookieValue(
                    name = RefreshTokenCookieProvider.COOKIE_NAME,
                    required = false //쿠키가 없을 때 Spring 기본 오류가 아니라 우리가 만든 오류 사용
            ) String refreshToken,
            HttpServletResponse response
    ) {
        SocialLoginResult result =
                authTokenService.refresh(refreshToken); //만들었던 서비스로 처리

        ResponseCookie refreshCookie =
                refreshTokenCookieProvider.createCookie( //재발급된 새 리프레시 토큰을 공통 설정으로 쿠키로 만듦
                        result.getRefreshToken()
                );

        response.addHeader( //응답 헤어데 쿠키 넣기
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );

        return ApiResponse.success( //액세스 토큰 반환
                result.getAccessTokenResponse()
        );
    }
}
