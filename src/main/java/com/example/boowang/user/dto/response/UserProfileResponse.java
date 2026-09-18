package com.example.boowang.user.dto.response;

import com.example.boowang.user.entity.User;
import lombok.Getter;

// 마이페이지에 필요한 정보만 반환한다.
@Getter
public class UserProfileResponse {

    private final String nickname; //닉네임
    private final String phone; //번호
    private final int trustScore; //신뢰도 점수
    private final TrustLevelResponse trustLevel; //신뢰도등급

    private UserProfileResponse(
            String nickname,
            String phone,
            int trustScore,
            TrustLevelResponse trustLevel
    ) {
        this.nickname = nickname;
        this.phone = phone;
        this.trustScore = trustScore;
        this.trustLevel = trustLevel;
    }

    // 조회한 사용자를 화면용 응답으로 변환한다.
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getNickname(),
                user.getPhone(),
                user.getTrustScore(),
                TrustLevelResponse.fromScore(user.getTrustScore())
        );
    }
}