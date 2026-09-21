package com.example.boowang.auth.service;

import com.example.boowang.auth.dto.response.AccessTokenResponse;
import com.example.boowang.global.security.jwt.JwtProperties;
import com.example.boowang.global.security.jwt.JwtTokenProvider;
import com.example.boowang.user.entity.User;
import com.example.boowang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

// 개발용 테스트 사용자를 준비하고 우리 서비스의 토큰을 발급한다.
@Service
@RequiredArgsConstructor
public class TestAuthService {

    // 테스트 로그인에서 항상 사용할 고정 닉네임
    private static final String TEST_NICKNAME = "테스트사용자";

    // 액세스 토큰을 Authorization 헤더에 넣을 때 사용하는 인증 방식
    private static final String TOKEN_TYPE = "Bearer";

    // 테스트 사용자를 조회하거나 처음 생성할 때 사용한다.
    private final UserRepository userRepository;

    // 우리 서비스의 JWT 액세스 토큰을 만들 때 사용한다.
    private final JwtTokenProvider jwtTokenProvider;

    // 액세스 토큰의 만료시간 설정을 가져온다.
    private final JwtProperties jwtProperties;
    // 테스트 사용자를 준비하고 JWT 액세스 토큰을 발급한다.

    @Transactional
    public AccessTokenResponse login() {
        // 기존 테스트 사용자를 찾거나 처음 생성한다.
        User testUser = findOrCreateTestUser(); //db에서 테스트 사용자를 찾고 없으면 새로 생성, 반환된 사용자 변수에 저장

        // 테스트 사용자 ID로 액세스 토큰을 만든다.
        String accessToken = jwtTokenProvider.createAccessToken(
                testUser.getId()
        );

        // 설정된 만료시간을 밀리초에서 초 단위로 바꾼다.
        long accessTokenExpirationSeconds =
                jwtProperties.getAccessTokenExpirationMs() / 1000;

        // Swagger와 프론트엔드에 전달할 액세스 토큰 응답을 만든다.
        return new AccessTokenResponse( // 완성된 응답을 controller로 돌려줌
                accessToken, //발급된 jwt
                TOKEN_TYPE, //Bearer
                accessTokenExpirationSeconds //토큰 유효시간
        );
    }
    // 탈퇴하지 않은 테스트 사용자를 찾고, 없으면 처음 생성한다.
    private User findOrCreateTestUser() {
        Optional<User> testUser =
                userRepository.findByNicknameIgnoreCaseAndDeletedAtIsNull(
                        TEST_NICKNAME
                );

        // 기존 테스트 사용자가 있으면 새로 만들지 않고 그대로 사용한다.
        if (testUser.isPresent()) {
            return testUser.get();
        }

        // 테스트 사용자가 처음 로그인한 경우 새 사용자로 저장한다.
        User newTestUser = User.create(TEST_NICKNAME);
        return userRepository.save(newTestUser);
    }

}
