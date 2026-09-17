package com.example.boowang.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
                        // 위에 적지 않은 나머지 주소는 모두 액세스 토큰이 필요하다.
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        // 소셜 인증에 성공하면 부왕 사용자 조회·가입을 처리한다.
                        .successHandler((request, response, authentication) -> {
                            // Spring이 검증한 소셜 인증 결과를 꺼낸다.
                            OidcUser socialUser = //검증된 소셜 인증 결과
                                    (OidcUser) authentication.getPrincipal();

                            response.setContentType("text/plain;charset=UTF-8");

                            try {
                                // 현재 연동한 제공자와 사용자 정보를 공통 서비스에 전달한다.
                                User user = socialLoginService.findOrCreateUser(
                                        SocialProvider.KAKAO, //현재 로그인한 제공자 종류
                                        socialUser.getSubject(), //소셜 사용자 고유번호 sub부분
                                        socialUser.getNickName() // 제공 받은 닉네임
                                );

                                // DB에 연결된 부왕 사용자 정보를 확인한다.
                                response.setStatus(200);
                                response.getWriter().write(
                                        "부왕 사용자 확인 성공"
                                                + "\n부왕 사용자 ID: " + user.getId()
                                                + "\n닉네임: " + user.getNickname()
                                );
                            } catch (BusinessException exception) {
                                // 탈퇴 사용자 등 서비스의 업무 오류를 응답한다.
                                response.setStatus(
                                        exception.getErrorCode().getHttpStatus().value()
                                );
                                response.getWriter().write(
                                        exception.getErrorCode().name()
                                                + "\n" + exception.getMessage()
                                );
                            }
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
