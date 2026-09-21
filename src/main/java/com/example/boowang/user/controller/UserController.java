package com.example.boowang.user.controller;

import com.example.boowang.place.dto.response.PlaceSummaryResponse; //즐겨찾기 장소 한개
import com.example.boowang.place.service.PlaceCommandService; //즐겨찾기 조회기능 호출

import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.security.AuthenticatedUser;
import com.example.boowang.global.security.token.RefreshTokenCookieProvider;
import com.example.boowang.user.dto.response.UserProfileResponse;
import com.example.boowang.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.boowang.user.dto.request.UserUpdateRequest;
import com.example.boowang.user.service.UserCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

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

    // Refresh Token 쿠키를 삭제할 때 사용한다.
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    // 장소 담당자가 작성한 즐겨찾기 조회 서비스를 사용한다.
    private final PlaceCommandService placeCommandService;

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
        return ApiResponse.success(profile); //공통응답으로 묶음
    }

    // 로그인한 사용자를 탈퇴 처리한다.
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpServletResponse response
    ) {
        // 사용자 탈퇴와 모든 Refresh Token 세션 폐기를 실행한다.
        userCommandService.withdraw(user.getUserId());

        // 현재 브라우저의 Refresh Token 쿠키를 삭제한다.
        ResponseCookie deleteCookie =
                refreshTokenCookieProvider.deleteCookie();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                deleteCookie.toString()
        );

        return ApiResponse.success(null);
    }

    //로그인한 사용자의 즐겨찾기 장소 목록 조회
    @GetMapping("/me/favorites")
    public ApiResponse<List<PlaceSummaryResponse>> getMyFavorites(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        List<PlaceSummaryResponse> favorites =
                placeCommandService.getFavorites(user.getUserId());
        return ApiResponse.success(favorites); //마찬가지로 공통응답으로 묶음
    }
}