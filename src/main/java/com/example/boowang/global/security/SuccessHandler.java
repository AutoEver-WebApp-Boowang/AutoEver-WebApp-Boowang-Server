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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    // 소셜 계정에 연결된 부왕 사용자를 조회하거나 가입시키는 서비스
    private final SocialLoginService socialLoginService;

    // 객체를 공통 JSON 응답으로 변환한다.
    private final ObjectMapper objectMapper;

    // 로그인과 재발급에서 같은 설정의 Refresh Token 쿠키를 사용한다.
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    // 소셜 로그인 성공 후 이동할 프론트엔드 주소
    @Value("${app.auth.login-success-url}")
    private String loginSuccessUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 어떤 소셜 로그인으로 인증되었는지 확인할 수 있는 토큰이다.
        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        // 카카오 또는 현대자동차가 보내준 사용자 정보를 꺼낸다.
        OAuth2User socialUser = oauthToken.getPrincipal();

        // application.yaml의 등록 이름인 kakao 또는 hyundai가 들어온다.
        String registrationId =
                oauthToken.getAuthorizedClientRegistrationId();

        SocialProvider provider;
        String socialNickname;

        // 로그인 제공자에 따라 사용자 이름을 꺼내는 위치가 다르다.
        if ("kakao".equals(registrationId)) {
            provider = SocialProvider.KAKAO;
            socialNickname = socialUser.getAttribute("nickname");
        } else if ("hyundai".equals(registrationId)) {
            provider = SocialProvider.HYUNDAI;
            socialNickname = socialUser.getAttribute("name");
        } else {
            response.setStatus(400);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8);

            objectMapper.writeValue(
                    response.getWriter(),
                    ApiResponse.error(
                            "UNSUPPORTED_SOCIAL_PROVIDER",
                            "지원하지 않는 소셜 로그인입니다."
                    )
            );
            return;
        }

        // user-name-attribute 설정에 따라 소셜 서비스의 사용자 고유번호를 가져온다.
        // 카카오는 sub, 현대자동차는 id가 사용된다.
        String providerUserId = socialUser.getName();

        // 로그인 성공 응답을 브라우저나 중간 서버가 저장하지 않게 한다.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

        try {
            // 회원 확인·가입, 세션 저장, 토큰 발급을 실행한다.
            SocialLoginResult result = socialLoginService.login(
                    provider,
                    providerUserId,
                    socialNickname
            );

            // 공통 쿠키 설정으로 Refresh Token 쿠키를 만든다.
            ResponseCookie refreshCookie =
                    refreshTokenCookieProvider.createCookie(
                            result.getRefreshToken()
                    );

            // Set-Cookie 헤더를 받으면 브라우저가 쿠키를 저장한다.
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    refreshCookie.toString()
            );

            // Refresh Token 쿠키를 저장한 뒤 프론트 로그인 완료 화면으로 이동한다.
            response.sendRedirect(loginSuccessUrl);
        } catch (BusinessException exception) {
            // 탈퇴한 회원 등의 업무 오류도 공통 JSON으로 보낸다.
            response.setStatus(
                    exception.getErrorCode().getHttpStatus().value()
            );
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8);

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