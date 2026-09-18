package com.example.boowang.user.dto.response;

import lombok.Getter;

// 신뢰도 등급 코드와 표시명을 전달한다.
@Getter
public class TrustLevelResponse {

    private final String code;
    private final String displayName;

    private TrustLevelResponse(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    // 서버에서 점수에 해당하는 등급을 계산한다.
    public static TrustLevelResponse fromScore(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("신뢰도 점수는 음수일 수 없습니다.");
        }

        if (score <= 100) {
            return new TrustLevelResponse("BEGINNER", "바린이");
        }
        if (score <= 200) {
            return new TrustLevelResponse("INTERMEDIATE", "쿼터라이더");
        }
        if (score <= 300) {
            return new TrustLevelResponse("EXPERT", "미들라이더");
        }
        return new TrustLevelResponse("LITER_RIDER", "리터라이더");
    }
}