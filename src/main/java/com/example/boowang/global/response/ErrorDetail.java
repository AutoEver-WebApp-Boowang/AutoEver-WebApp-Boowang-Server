package com.example.boowang.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

// 실패 응답의 에러 코드, 안내 문구, 잘못된 입력 필드를 담는다.
@Getter
@RequiredArgsConstructor
public class ErrorDetail {

    // 프론트엔드가 오류 종류를 구분할 이름이다. 예: USER_NOT_FOUND
    private final String code;
    // 화면에 표시할 전체 오류 안내 문구이다.
    private final String message;
    // 입력 필드 오류 목록이다. 일반 오류일 때는 빈 목록이 들어간다.
    private final List<FieldErrorResponse> fieldErrors;
}
