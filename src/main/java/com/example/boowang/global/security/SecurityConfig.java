package com.example.boowang.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.boowang.global.security.jwt.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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

    private final SuccessHandler successHandler;

    private final FailureHandler failureHandler;

    // 모든 HTTP 요청이 통과하는 Spring Security 필터들의 규칙을 만든다.
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient
    ) throws Exception {
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
                // Swagger, 소셜 로그인, 테스트 로그인, 토큰 관리 주소는 로그인 없이 접근할 수 있다.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/test-auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/error",
                                "/favicon.ico"
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
                        .tokenEndpoint(token ->
                                token.accessTokenResponseClient(accessTokenResponseClient)
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                // 아이디·비밀번호 인증 필터보다 먼저 JWT 인증 필터를 실행한다.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // 위에서 작성한 규칙으로 실제 Security 필터 묶음을 완성한다.
        return http.build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        OAuth2ErrorHttpMessageConverter errorConverter = new OAuth2ErrorHttpMessageConverter();
        errorConverter.setErrorConverter(parameters -> {
            String errorCode = parameters.get("error");
            if (errorCode == null || errorCode.isBlank()) {
                errorCode = parameters.get("errCode");
            }
            if (errorCode == null || errorCode.isBlank()) {
                errorCode = "oauth2_provider_error";
            }

            String errorDescription = parameters.get("error_description");
            if (errorDescription == null || errorDescription.isBlank()) {
                errorDescription = parameters.get("errMsg");
            }

            String errorUri = parameters.get("error_uri");
            return new OAuth2Error(errorCode, errorDescription, errorUri);
        });

        OAuth2ErrorResponseErrorHandler errorHandler = new OAuth2ErrorResponseErrorHandler();
        errorHandler.setErrorConverter(errorConverter);

        RestClient restClient = RestClient.builder()
                .configureMessageConverters(messageConverters -> {
                    messageConverters.addCustomConverter(new FormHttpMessageConverter());
                    messageConverters.addCustomConverter(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(errorHandler)
                .build();

        RestClientAuthorizationCodeTokenResponseClient responseClient =
                new RestClientAuthorizationCodeTokenResponseClient();
        responseClient.setRestClient(restClient);
        return responseClient;
    }
}
