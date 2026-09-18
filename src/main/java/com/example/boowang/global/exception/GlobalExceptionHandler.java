package com.example.boowang.global.exception;

import com.example.boowang.global.response.ApiResponse;
import com.example.boowang.global.response.FieldErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.List;
import java.util.Objects;

// Controller 실행 중 발생한 예외를 잡아 공통 ApiResponse JSON으로 바꾼다.
// @Slf4j는 예상하지 못한 서버 오류를 콘솔 로그에 남길 수 있게 해준다.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Service에서 BusinessException을 던지면 등록된 ErrorCode의 상태와 문구를 사용한다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(
                        errorCode.name(),
                        errorCode.getMessage(),
                        exception.getFieldErrors()
                ));
    }

    // @Valid 검증에 실패했을 때 어떤 입력 필드가 왜 틀렸는지 목록으로 만든다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        // Spring이 수집한 필드 오류를 우리 응답 형식인 FieldErrorResponse로 변환한다.
        List<FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorResponse(
                        fieldError.getField(),
                        Objects.requireNonNullElse(
                                fieldError.getDefaultMessage(),
                                "올바른 값을 입력해 주세요."
                        )
                ))
                .toList();

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(
                        errorCode.name(),
                        errorCode.getMessage(),
                        fieldErrors
                ));
    }

    // JSON의 쉼표나 자료형 등이 잘못되어 요청 본문을 읽을 수 없을 때 처리한다.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableRequest(
            HttpMessageNotReadableException exception
    ) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.name(), errorCode.getMessage()));
    }

    // 위에서 따로 처리하지 못한 모든 오류가 사용자에게 상세하게 노출되지 않도록 막는다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception
    ) {
        // 개발자는 원인을 확인할 수 있도록 서버 로그에는 전체 오류 내용을 남긴다.
        log.error("처리하지 못한 서버 오류가 발생했습니다.", exception);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.name(), errorCode.getMessage()));
    }
}
