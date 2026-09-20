package com.example.boowang.global.security;

import com.example.boowang.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
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
    }
}
