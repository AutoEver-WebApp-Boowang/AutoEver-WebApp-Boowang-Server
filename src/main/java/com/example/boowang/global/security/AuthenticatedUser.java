package com.example.boowang.global.security;

import lombok.Getter;

// JWT 인증이 끝난 사용자의 최소 정보만 Spring Security 안에 보관한다.
// 닉네임이나 신뢰도는 바뀔 수 있으므로 JWT 사용자 정보에 넣지 않는다.
@Getter
public class AuthenticatedUser {

    // users 테이블에서 현재 사용자를 찾을 때 쓰는 ID
    private final Long userId;

    // JWT에서 꺼낸 사용자 ID를 저장한다.
    public AuthenticatedUser(Long userId) {
        this.userId = userId;
    }
}