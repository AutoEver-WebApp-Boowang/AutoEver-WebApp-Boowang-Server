package com.example.boowang.auth.service;

import com.example.boowang.auth.dto.SocialLoginResult;
import com.example.boowang.auth.dto.response.AccessTokenResponse;
import com.example.boowang.auth.entity.AuthSession;
import com.example.boowang.auth.repository.AuthSessionRepository;
import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.global.security.jwt.JwtProperties;
import com.example.boowang.global.security.jwt.JwtTokenProvider;
import com.example.boowang.global.security.token.RefreshTokenProvider;
import com.example.boowang.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthSessionRepository authSessionRepository; //세션조회, 저장
    private final RefreshTokenProvider refreshTokenProvider; //리프레시토큰 생성,해시
    private final JwtTokenProvider jwtTokenProvider; //액세스 토큰 생성
    private final JwtProperties jwtProperties; //액세스, 리프레시 토큰 유효시간 제공

    // 유효한 Refresh Token을 새 토큰 묶음으로 교체한다.
    @Transactional
    public SocialLoginResult refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) { //쿠키 자체가 없음 또는 값이 비어있거나 공백
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING); //이경우엔 db조회없이 바로 401반환
        }

        String refreshTokenHash =
                refreshTokenProvider.hashToken(refreshToken); //db에는 리프레시 토큰 원본이 없으므로

        AuthSession currentSession = authSessionRepository
                .findByRefreshTokenHashAndRevokedAtIsNull(refreshTokenHash) //해시가 일치하면서 아직 로그아웃되지 않은 세션찾기
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID)
                );

        if (currentSession.isExpired()) { //만료검사
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED); //마찬가지로 만료되면 401 반환
        }

        User user = currentSession.getUser(); //세션의 회원을 꺼내와서

        if (user.getDeletedAt() != null) { //탈퇴한 회원인지 검사 소프트딜리트 되어있는지
            throw new BusinessException(ErrorCode.USER_WITHDRAWN);
        }

        // 기존 Refresh Token을 다시 사용할 수 없도록 현재 세션을 폐기한다.
        currentSession.revoke();

        // 같은 사용자에게 새 세션과 새 토큰 묶음을 발급한다.
        return issue(user);
    }

    // 로그인 또는 재발급에 사용할 새 세션과 토큰 묶음을 만든다.
    @Transactional //새토큰 발급 도중 오류 발생시에 함께 취소
    public SocialLoginResult issue(User user) {
        String newRefreshToken =
                refreshTokenProvider.generateToken(); //브라우저 쿠키에 넣을 원본

        String newRefreshTokenHash =
                refreshTokenProvider.hashToken(newRefreshToken); //db에 해시로 변환해 저장

        LocalDateTime expiresAt = LocalDateTime.now().plus(
                Duration.ofMillis(
                        jwtProperties.getRefreshTokenExpirationMs() //현재시각 + 리프레시 토큰 유지시간 = 만료시각
                )
        );

        AuthSession newSession = authSessionRepository.save( //새 로그인 세션을 저장
                AuthSession.create(
                        user,
                        newRefreshTokenHash,
                        expiresAt
                )
        );

        String newAccessToken =
                jwtTokenProvider.createAccessToken( //새 액세스 토큰 생성
                        user.getId(), //회원번호
                        newSession.getId() //새 세션번호
                );

        AccessTokenResponse accessTokenResponse =
                new AccessTokenResponse(
                        newAccessToken,
                        "Bearer",
                        jwtProperties.getAccessTokenExpirationMs() / 1000
                );

        return new SocialLoginResult(
                accessTokenResponse, //json응답에 사용
                newRefreshToken //httponly쿠키에 사용
        );
    }
}