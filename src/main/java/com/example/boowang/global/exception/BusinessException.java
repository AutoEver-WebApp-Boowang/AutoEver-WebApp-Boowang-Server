package com.example.boowang.global.exception;

import lombok.Getter;

// 예상 가능한 업무 오류를 Service에서 일부러 발생시킬 때 사용한다.
// 예: 사용자가 없으면 new BusinessException(ErrorCode.USER_NOT_FOUND)을 던진다.
@Getter
public class BusinessException extends RuntimeException {

    // 어떤 오류인지 GlobalExceptionHandler가 확인할 수 있도록 보관한다.
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        // 부모 예외에도 사람이 읽을 수 있는 오류 메시지를 전달한다.
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
