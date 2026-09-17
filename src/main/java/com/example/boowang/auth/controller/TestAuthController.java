package com.example.boowang.auth.controller;

import com.example.boowang.auth.dto.response.AccessTokenResponse;
import com.example.boowang.auth.service.TestAuthService;
import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.AuthenticatedUser; //JWT 필터가 저장한 userId, sessionId를 꺼내기 위함
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

//일단 sns로그인 구현 전에 사용하는 임시 로그인 API이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-auth")
public class TestAuthController {

    //테스트 사용자와 액세스 토큰을 만드는 서비스 사용
    private final TestAuthService testAuthService;

    //테스트 사용자로 로그인하고 JWT 액세스 토큰을 발급한다.
    @PostMapping("/login")
    public ApiResponse<AccessTokenResponse> login() {
        AccessTokenResponse accessTokenResponse =
                testAuthService.login();

        return ApiResponse.success(accessTokenResponse);//내가 만들어놓은 프로젝트 공통 성공 응답 형식으로 감싼다
    }
    //jwt필터가 현재 사용자를 정상적으로 등록했는지 확인
    // Swagger에서 이 API가 JWT 액세스 토큰을 사용하는 API임을 표시한다.
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ApiResponse<Map<String, Long>> getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        //인증 정보 안에서 우리 사용자 정보를 꺼냄
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        //사용자 아이디, 세션아이디 map에 넣어서 보기 쉽게
        Map<String, Long> userInformation = new HashMap<>();
        userInformation.put(
                "userId",
                authenticatedUser.getUserId()
        );
        userInformation.put(
                "sessionId",
                authenticatedUser.getSessionId()
        );

        return ApiResponse.success(userInformation);
    }

}
