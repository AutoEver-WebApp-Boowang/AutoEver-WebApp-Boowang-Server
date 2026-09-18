package com.example.boowang.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.boowang.global.security.jwt.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.boowang.auth.service.SocialLoginService;
import com.example.boowang.auth.entity.SocialProvider;
import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.user.entity.User;

import com.example.boowang.auth.dto.SocialLoginResult;
import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
// 어떤 주소를 공개하고 어떤 주소에 로그인을 요구할지 정하는 보안 설정이다.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // CorsConfig에서 만든 프론트엔드 요청 허용 규칙을 주입받는다.
    private final CorsConfigurationSource corsConfigurationSource;
    // 로그인하지 않은 요청에 401 공통 JSON을 보내는 처리기이다.
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    // 권한이 부족한 요청에 403 공통 JSON을 보내는 처리기이다.
    private final RestAccessDeniedHandler accessDeniedHandler;

    //http 요청의 액세스 토큰을 검사하는 jwt 인증 필터이다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //소셜 계정에 연결된 부왕 사용자를 조회하거나 가입시키는 서비스
    private final SocialLoginService socialLoginService;

    // 객체를 공통 JSON 응답으로 변환한다.
    private final ObjectMapper objectMapper;

    // 쿠키 유지 시간을 토큰 유효시간에 맞춘다.
    private final JwtProperties jwtProperties;

    //YAML에서 환경별 쿠키 설정을 읽는다.
    @Value("${app.auth.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.auth.cookie-same-site}")
    private String cookieSameSite;

    // 모든 HTTP 요청이 통과하는 Spring Security 필터들의 규칙을 만든다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT를 사용하므로 서버 세션 기반 CSRF 보호는 사용하지 않는다.
                .csrf(AbstractHttpConfigurer::disable)

                // CorsConfig에서 작성한 CORS 규칙을 Spring Security에도 적용한다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 로그인 정보를 서버 메모리 세션에 저장하지 않는다.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 인증 실패와 권한 부족 응답을 우리가 만든 JSON 처리기로 연결한다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // Swagger, 소셜 로그인, 테스트 로그인 주소는 로그인 없이 접근할 수 있다.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/test-auth/login",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/places",
                                "/api/places/search",
                                "/api/places/*",
                                "/api/places/*/reviews"
                        ).permitAll()


                        // 위에 적지 않은 나머지 주소는 모두 액세스 토큰이 필요하다.
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        // 소셜 인증 성공 후 부왕 세션과 토큰을 발급한다.
                        .successHandler((request, response, authentication) -> {
                            // 현재 카카오 OIDC에서 검증된 사용자 정보를 꺼낸다.
                            OidcUser socialUser =
                                    (OidcUser) authentication.getPrincipal();

                            // 토큰 응답을 JSON으로 보내고 캐시에 저장하지 않게 한다.
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

                            try {
                                // 회원 확인·가입, 세션 저장, 토큰 발급을 실행한다.
                                SocialLoginResult result = socialLoginService.login(
                                        SocialProvider.KAKAO,
                                        socialUser.getSubject(),
                                        socialUser.getNickName()
                                );

                                // 리프레시 토큰 원본을 HttpOnly 쿠키로 만든다.
                                ResponseCookie refreshCookie = //쿠키의 이름,값,속성 구성
                                        ResponseCookie.from(
                                                        "refreshToken",
                                                        result.getRefreshToken()
                                                )
                                                .httpOnly(true)
                                                .secure(cookieSecure)
                                                .sameSite(cookieSameSite)
                                                .path("/api/v1/auth") //앞으로 만들 재발급/로그아웃 API에 쿠키를 보내기 위한 범위
                                                .maxAge(Duration.ofMillis(
                                                        jwtProperties.getRefreshTokenExpirationMs()
                                                ))
                                                .build();

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
                        })
                        .failureHandler((request, response, exception) -> {
                            // Spring이 전달한 소셜 인증 실패 코드를 꺼낸다.
                            String errorCode = "SOCIAL_LOGIN_FAILED";
                            if (exception instanceof OAuth2AuthenticationException) {
                                OAuth2AuthenticationException socialException =
                                        (OAuth2AuthenticationException) exception;
                                errorCode = socialException.getError().getErrorCode();
                            }

                            // 실패 페이지로 이동하지 않고 오류를 JSON으로 반환한다.
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
                            objectMapper.writeValue(
                                    response.getWriter(),
                                    ApiResponse.error(
                                            errorCode,
                                            "소셜 로그인 인증에 실패했습니다."
                                    )
                            );
                        })
                )
                // 아이디·비밀번호 인증 필터보다 먼저 JWT 인증 필터를 실행한다.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // 위에서 작성한 규칙으로 실제 Security 필터 묶음을 완성한다.
        return http.build();
    }
}
