package com.example.boowang.global.security;

import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 로그인이 필요한 API를 Access Token 없이 호출했을 때 실행된다.
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // ApiResponse 객체를 실제 JSON 문자열로 바꾸는 도구를 Spring이 넣어준다.
    private final ObjectMapper objectMapper;

    // Spring Security가 인증되지 않은 요청을 발견하면 이 메서드를 자동으로 호출한다.
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = ErrorCode.ACCESS_TOKEN_MISSING;

        // HTTP 상태는 401로 설정하고 본문은 우리 공통 실패 JSON으로 작성한다.
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.error(errorCode.name(), errorCode.getMessage())
        );
    }
}
