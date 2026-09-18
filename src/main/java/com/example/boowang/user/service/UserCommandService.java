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

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;

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
}