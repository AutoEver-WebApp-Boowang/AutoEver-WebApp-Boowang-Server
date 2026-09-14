package com.example.boowang.global.response;

import lombok.Getter;

import java.util.List;

//{
//        "success": true,
//        "data": {},
//        "error": null
//        }
//이렇게 아니면
//{
//        "success": false,
//        "data": null,
//        "error": {
//        "code": "USER_NOT_FOUND",
//        "message": "사용자를 찾을 수 없습니다.",
//        "fieldErrors": []
//        }
//        }
//이렇게 오게

@Getter
public class ApiResponse<T> {

    // 요청 성공 여부: 성공이면 true, 실패이면 false
    private final boolean success;
    // 성공했을 때 실제로 전달할 데이터. 실패하면 null이다.
    private final T data;
    // 실패했을 때 전달할 오류 정보. 성공하면 null이다.
    private final ErrorDetail error;

    // 성공 여부와 data/error 조합을 강제로 지키기 위해 밖에서 직접 호출하지 못하게 한다.
    private ApiResponse(boolean success, T data, ErrorDetail error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // 성공 응답을 만든다. 예: ApiResponse.success(userResponse)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 특정 입력 필드와 관계없는 일반 실패 응답을 만든다.
    public static ApiResponse<Void> error(
            String code,
            String message
    ) {
        ErrorDetail errorDetail =
                new ErrorDetail(code, message, List.of());

        return new ApiResponse<>(false, null, errorDetail);
    }

    // nickname처럼 어떤 입력 필드가 잘못됐는지 포함하는 실패 응답을 만든다.
    public static ApiResponse<Void> error(
            String code,
            String message,
            List<FieldErrorResponse> fieldErrors
    ) {
        ErrorDetail errorDetail =
                new ErrorDetail(code, message, fieldErrors);

        return new ApiResponse<>(false, null, errorDetail);
    }
}
