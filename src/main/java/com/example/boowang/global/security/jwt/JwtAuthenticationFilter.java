package com.example.boowang.global.security.jwt;

import com.example.boowang.global.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//요청의 Access Token을 검사하고 로그인 사용자 정보를 스프링 시큐리티에 등록
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer "; //띄어쓰기 필수, 뒤에 토큰이름 와야함
                                                            //띄어쓰기 포함 7번째 인덱스까지 아래에서 잘라야됨
    //JWT의 서명과 만료시간을 검증하고 토큰 내용을 읽는다.
    private final JwtTokenProvider jwtTokenProvider;

    //HTTP 요청 한 번 마다 한 번만 실행
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, //요청부분을 쉽게 뜯게해주는 객체(클래스의 인스턴스)
            HttpServletResponse response, //응답을 쉽게 생성해주는 객체
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);

        //Access Token이 있는 요청만 인증 정보를 만든다. 토큰 안에 담기는 정보가 클레임
        if(accessToken != null) {
            try {
                Claims claims = jwtTokenProvider.parseClaims(accessToken); //액세스 토큰에서 클레임 추출

                AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                        jwtTokenProvider.getUserId(claims),
                        jwtTokenProvider.getSessionId(claims)
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                authenticatedUser, //1. 로그인한 사람이 누구인지?
                                null, //2. 비밀번호 같은 인증 수단, 이미 앞에서 JWT의 서명과 만료시간을 검사했으니 null이다.
                                List.of() //3. 사용자의 권한 목록
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException exception) {
                // 위조됐거나 만료된 토큰이면 로그인되지 않은 요청으로 처리한다.
                SecurityContextHolder.clearContext();
            }
        }

        // 인증 처리 후 다음 필터로 요청을 전달한다.
        filterChain.doFilter(request, response);
    }

    // Authorization: Bearer {Access Token} 형식에서 토큰 부분만 꺼낸다.
    private String resolveAccessToken(HttpServletRequest request) {
        String authorizationHeader =
                request.getHeader(AUTHORIZATION_HEADER); //http헤더중에 필요로하는 부분을 가져옴

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_PREFIX)) { //근데 값이 없거나, 베어러로 시작을 안하면
            return null;                                               // 널 주기
        }

        return authorizationHeader.substring(BEARER_PREFIX.length()); // 제대로 있으면 베어러를 뜯어내고 jwt 액세스토큰부분만 가져옴
    }                                       //앞에서부터 베어러 길이만큼 잘라냄
}