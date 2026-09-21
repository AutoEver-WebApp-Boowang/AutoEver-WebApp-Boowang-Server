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
@Tag(name = "Authentication", description = "토큰 재발급 및 로그아웃")
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
    // 현재 브라우저의 Refresh Token을 폐기하고 쿠키를 삭제한다.
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @CookieValue(
                    name = RefreshTokenCookieProvider.COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletResponse response
    ) {
        // DB의 해당 Refresh Token 세션을 폐기한다.
        authTokenService.logout(refreshToken);

        // 브라우저의 Refresh Token 쿠키를 삭제하는 응답 쿠키를 만든다.
        ResponseCookie deleteCookie =
                refreshTokenCookieProvider.deleteCookie();

        // Set-Cookie 응답 헤더로 만료된 쿠키를 전달한다.
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                deleteCookie.toString()
        );

        return ApiResponse.success(null);
    }
}
