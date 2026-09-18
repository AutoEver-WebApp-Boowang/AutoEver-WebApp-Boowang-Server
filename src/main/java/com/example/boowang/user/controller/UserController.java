package com.example.boowang.user.controller;

import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.AuthenticatedUser;
import com.example.boowang.user.dto.response.UserProfileResponse;
import com.example.boowang.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.boowang.user.dto.request.UserUpdateRequest;
import com.example.boowang.user.service.UserCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 로그인한 사용자의 정보를 관리
@RestController //json
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "My Page", description = "내 정보 관리")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    //작성했던 사용자 조회 서비스를 사용한다.
    private final UserQueryService userQueryService;

    // 사용자의 정보를 수정하는 서비스를 사용한다.
    private final UserCommandService userCommandService;

    //로그인한 사용자의 마이페이지 정보를 조회한다.
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        // JWT 필터가 인증 정보에 저장한 회원 번호를 사용한다.
        UserProfileResponse profile =
                userQueryService.getMyProfile(user.getUserId());//인증된 회원 번호로 기존 getMyProfile()호출
        return ApiResponse.success(profile); //기존공통 응답으로 감싸 반환
    }

    // 로그인한 사용자의 닉네임과 휴대폰 번호를 수정한다.
    @PatchMapping("/me")
    public ApiResponse<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserProfileResponse profile =
                userCommandService.updateMyProfile(user.getUserId(), request);
        return ApiResponse.success(profile);
    }
}