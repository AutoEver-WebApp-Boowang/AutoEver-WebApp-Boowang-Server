package com.example.boowang.auth.repository;

import com.example.boowang.auth.entity.SocialAccount;
import com.example.boowang.auth.entity.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


//소셜 로그인 계정 정보를 DB에서 조회하는 곳
// JpaRepository를 상속하면 save(), findById() 같은 기본적인 DB 기능을 사용 가능 수업시간에 배운 부분
public interface SocialAccountRepository
        extends JpaRepository<SocialAccount, Long> {

//소셜 로그인 종류와 소셜 사용자 고유번호로 계정을 찾는다.
//예시:
//provider = KAKAO
//providerUserId = 카카오에서 받은 사용자 고유번호 결과가 없을 수도 있으므로 Optional로 받는다> null방지용
    Optional<SocialAccount> findByProviderAndProviderUserId(
            SocialProvider provider,
            String providerUserId
    );
}