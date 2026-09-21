package com.example.boowang.user.service;

import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.global.response.FieldErrorResponse;
import com.example.boowang.user.dto.request.UserUpdateRequest;
import com.example.boowang.user.dto.response.UserProfileResponse;
import com.example.boowang.user.entity.User;
import com.example.boowang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.boowang.auth.entity.AuthSession;
import com.example.boowang.auth.repository.AuthSessionRepository;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;

    private final AuthSessionRepository authSessionRepository;

    // 로그인한 사용자가 보낸 필드만 수정한다.
    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.hasNickname()) {
            String nickname = request.getNickname();

            if (nickname == null) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_ERROR,
                        List.of(new FieldErrorResponse("nickname", "닉네임은 null로 변경할 수 없습니다."))
                );
            }

            if (userRepository.existsByNicknameIgnoreCaseAndIdNot(nickname, user.getId())) {
                throw new BusinessException(
                        ErrorCode.NICKNAME_ALREADY_EXISTS,
                        List.of(new FieldErrorResponse(
                                "nickname", ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage()
                        ))
                );
            }

            user.changeNickname(nickname);
        }

        if (request.hasPhone()) {
            user.changePhone(request.getPhone());
        }

        return UserProfileResponse.from(user);
    }
    // 로그인한 사용자를 탈퇴 처리하고 모든 Refresh Token 세션을 폐기한다.
    @Transactional
    public void withdraw(Long userId) {
        // 탈퇴 여부까지 확인하기 위해 삭제되지 않은 조건 없이 사용자를 조회한다.
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        // 이미 탈퇴한 사용자가 다시 요청하면 중복 탈퇴 오류를 반환한다.
        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_WITHDRAWN);
        }

        // users.deleted_at에 현재 시각을 기록한다.
        user.withdraw();

        // PC, 휴대폰 등에서 발급된 사용자의 모든 활성 세션을 조회한다.
        List<AuthSession> activeSessions =
                authSessionRepository.findAllByUser_IdAndRevokedAtIsNull(userId);

        // 조회된 모든 Refresh Token 세션에 폐기 시각을 기록한다.
        activeSessions.forEach(authSession -> authSession.revoke());
    }
}