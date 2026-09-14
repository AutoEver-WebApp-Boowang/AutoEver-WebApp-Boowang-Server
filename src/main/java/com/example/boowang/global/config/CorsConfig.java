package com.example.boowang.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// 프론트엔드 주소와 백엔드 주소가 달라도 API를 호출할 수 있게 허용 범위를 정한다.
@Configuration
public class CorsConfig {

    // @Bean으로 등록하면 Spring Security가 아래 CORS 규칙을 가져다 쓸 수 있다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            // 환경변수가 없으면 React/Vite의 기본 개발 주소인 localhost:5173을 사용한다.
            @Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin
    ) {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 프론트엔드 주소를 정확히 하나 지정한다. 쿠키 사용 시 *는 사용할 수 없다.
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        // 우리 API에서 사용할 HTTP 요청 방식만 허용한다.
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        // JWT와 JSON 요청에 필요한 헤더를 허용한다.
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // 브라우저가 Refresh Token 쿠키를 백엔드로 보낼 수 있게 한다.
        configuration.setAllowCredentials(true);
        // 브라우저가 CORS 허용 결과를 1시간 동안 기억하게 한다.
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 위 규칙을 백엔드의 모든 주소에 적용한다.
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
