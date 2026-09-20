package com.example.boowang.global.security;

import com.example.boowang.auth.dto.SocialLoginResult;
import com.example.boowang.auth.entity.SocialProvider;
import com.example.boowang.auth.service.SocialLoginService;
import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.token.RefreshTokenCookieProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    //소셜 계정에 연결된 부왕 사용자를 조회하거나 가입시키는 서비스
    private final SocialLoginService socialLoginService;

    // 객체를 공통 JSON 응답으로 변환한다.
    private final ObjectMapper objectMapper;

    // 로그인과 재발급에서 같은 설정의 Refresh Token 쿠키를 사용한다.
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 현재 카카오 OIDC에서 검증된 사용자 정보를 꺼낸다.
        OidcUser socialUser =
                (OidcUser) authentication.getPrincipal();

        // 토큰 응답을 JSON으로 보내고 캐시에 저장하지 않게 한다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

        try {
            // 회원 확인·가입, 세션 저장, 토큰 발급을 실행한다.
            SocialLoginResult result = socialLoginService.login(
                    SocialProvider.KAKAO,
                    socialUser.getSubject(),
                    socialUser.getNickName()
            );

            // 공통 쿠키 설정으로 Refresh Token 쿠키를 만든다.
            ResponseCookie refreshCookie =
                    refreshTokenCookieProvider.createCookie(
                            result.getRefreshToken()
                    );

            // Set-Cookie 헤더를 받으면 브라우저가 쿠키를 저장한다.>javascript로는 읽을 수 없다
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    refreshCookie.toString()
            );

            // 액세스 토큰 정보만 공통 JSON 응답으로 보낸다.
            response.setStatus(200);
            objectMapper.writeValue(
                    response.getWriter(),
                    ApiResponse.success(
                            result.getAccessTokenResponse() //이거만 JSON으로 보내므로 리프레시 토큰은 응답에 없음
                    )
            );
        } catch (BusinessException exception) {
            // 탈퇴한 회원 등의 업무 오류도 공통 JSON으로 보낸다.
            response.setStatus(
                    exception.getErrorCode().getHttpStatus().value()
            );
            objectMapper.writeValue(
                    response.getWriter(),
                    ApiResponse.error(
                            exception.getErrorCode().name(),
                            exception.getMessage()
                    )
            );
        }
    }
}
