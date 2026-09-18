package com.example.boowang.user.service;

import com.example.boowang.user.dto.response.UserProfileResponse;
import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.user.entity.User;
import com.example.boowang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사용자 정보를 조회하는 서비스
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    // 데이터를 변경하지 않으므로 읽기 전용으로 실행한다.
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
// 인증된 회원 번호로 탈퇴하지 않은 사용자만 조회한다.
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 화면에 필요한 정보만 반환한다.
        return UserProfileResponse.from(user);
    }
}