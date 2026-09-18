package com.example.boowang.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 프로젝트에서 사용할 오류의 HTTP 상태와 안내 문구를 한곳에 모아둔 목록이다.
// 문자열을 여러 파일에 직접 쓰지 않아서 오타와 서로 다른 문구가 생기는 것을 막는다.
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400: 사용자가 보낸 요청값이나 JSON 형식이 잘못된 경우
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값을 확인해 주세요."),
    PLACE_UPDATE_REQUEST_EMPTY(HttpStatus.BAD_REQUEST, "수정할 정보를 입력해 주세요."),

    // 401: 로그인 토큰 또는 로그인 세션에 문제가 있는 경우
    ACCESS_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "Access Token이 없습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Access Token이 올바르지 않습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "Refresh Token이 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Refresh Token이 올바르지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
    AUTH_SESSION_REVOKED(HttpStatus.UNAUTHORIZED, "이미 종료된 로그인 세션입니다."),
    USER_WITHDRAWN(HttpStatus.UNAUTHORIZED, "탈퇴한 사용자입니다."),

    // 403: 로그인은 했지만 해당 작업을 할 권한이 없는 경우
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다."),

    // 404: 요청한 사용자를 DB에서 찾을 수 없는 경우
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다."),
    PARKING_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소의 주차 정보를 찾을 수 없습니다."),

    // 409: 현재 DB 상태와 요청이 충돌하는 경우
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "이미 탈퇴한 사용자입니다."),
    REVIEW_ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 즐겨찾기한 장소입니다."),



    // 500: 미리 예상하지 못한 서버 오류가 발생한 경우
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    // 브라우저에 보낼 HTTP 상태 번호이다. 예: NOT_FOUND는 404이다.
    private final HttpStatus httpStatus;
    // 사용자에게 보여줄 기본 안내 문구이다.
    private final String message;
}