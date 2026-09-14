package com.example.boowang.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 요청값 검증에 실패한 필드 하나의 이름과 이유를 담는다.
@Getter
@RequiredArgsConstructor
public class FieldErrorResponse {

    // 잘못 입력한 DTO 필드 이름이다. 예: nickname
    private final String field;
    // 해당 필드가 검증에 실패한 이유이다.
    private final String message;

}
