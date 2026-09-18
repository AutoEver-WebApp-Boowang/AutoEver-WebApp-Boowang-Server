package com.example.boowang.global.security.jwt;

import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.util.Date;
import io.jsonwebtoken.JwtParser;

import io.jsonwebtoken.Claims;
//이 클래스는 우리 비밀키로 서명됐는지, 발급자가 부왕인지, 토큰이 만료되지는 않았는지 확인
//액세스 토큰에 사용할 서명 키와 설정을 준비
@Component
public class JwtTokenProvider {
    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    // JWT의 서명을 검증하고 내용을 읽는 객체
    private final JwtParser jwtParser;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties. getSecret());

        this. signingKey = Keys.hmacShaKeyFor(keyBytes);
        // 같은 비밀키로 서명된 Access Token만 검증
        this.jwtParser = Jwts.parser()
                .verifyWith(this.signingKey)
                //발급자 검사 추가
                .requireIssuer("boowang")
                .build();
        this. accessTokenExpirationMs = jwtProperties.getAccessTokenExpirationMs();
    }

    // 사용자와 로그인 세션 정보를 담은 Access Token을 만든다.
    public String createAccessToken(Long userId, Long sessionId) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(accessTokenExpirationMs);

        return Jwts.builder()
                .subject(userId.toString()) //sub에 부왕 회원 번호 저장
                .claim("sid", sessionId.toString()) //sid에 로그인 세선 번호 저장
                .issuer("boowang")
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();

    }

    // 서명과 발급자, 만료시간을 검증하고 토큰 정보를 반환
    public Claims parseClaims(String accessToken) {
        return jwtParser.parseSignedClaims(accessToken).getPayload();
    }

    //사용자 ID는 JWT의 sub에 들어있음
    // 토큰 정보에서 사용자 ID를 꺼낸다.
    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    //로그인 세션 ID는 JWT의 sid에 들어있음
    // 토큰 정보에서 로그인 세션 ID를 꺼낸다.
    public Long getSessionId(Claims claims) {
        return Long.valueOf(claims.get("sid", String.class));
    }
}

