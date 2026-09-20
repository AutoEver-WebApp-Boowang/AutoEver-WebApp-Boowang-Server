package com.example.boowang.auth.service;

import com.example.boowang.auth.entity.SocialAccount;
import com.example.boowang.auth.entity.SocialProvider;
import com.example.boowang.auth.repository.SocialAccountRepository;
import com.example.boowang.global.exception.BusinessException;
import com.example.boowang.global.exception.ErrorCode;
import com.example.boowang.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

import com.example.boowang.user.repository.UserRepository;

import com.example.boowang.auth.dto.SocialLoginResult;

@Service
@RequiredArgsConstructor
//소셜 계정과 연결된 부왕 사용자를 확인
public class SocialLoginService {

    // 닉네임의 랜덤 부분에 사용할 문자 목록 (대문자 26,숫자10)
    private static final String NICKNAME_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // 랜덤 숫자를 생성할 객체
    private final SecureRandom secureRandom = new SecureRandom();

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;

    // 로그인과 재발급에서 공통으로 사용할 토큰 발급 서비스
    private final AuthTokenService authTokenService;

    //조회만 하므로 읽기 전용 트렌잭션을 사용한다.
    @Transactional(readOnly = true)
    public Optional<User> findExistingUser(
            SocialProvider provider, //예를 들면 SocialProvider.KAKAO
            String providerUserId //소셜 서비스가 알려준 고유번호
    ) {
        //소셜 서비스 종류와 고유번호로 기존 연결을 찾는다
        Optional<SocialAccount> socialAccount =
                socialAccountRepository.findByProviderAndProviderUserId(
                        provider,
                        providerUserId
                );
        //연결이 없으면 처음 가입하는 사용자다. 즉 이게 회원가입인 셈
        if(socialAccount.isEmpty()){
            return Optional.empty();
        }
        //소셜 계정에 연결된 부왕 사용자를 꺼낸다.
        User user = socialAccount.get().getUser();

        //탈퇴한 계정은 재가입시키지 않는다.
        if(user.getDeletedAt() != null){
            throw new BusinessException(ErrorCode.USER_WITHDRAWN); //여기서 걸리면
        }
        return Optional.of(user); //탈퇴 예외가 발생하면 실행 안됨
    }
    // 소셜 닉네임을 정리하고 랜덤 문자를 붙일 공간을 확보한다.
    private String prepareNicknameBase(String socialNickname) {
        // 닉네임을 받지 못하면 빈 문자열부터 시작한다.
        String nickname = socialNickname == null ? "" : socialNickname;

        // 한글 완성형, 영문, 숫자, 밑줄 이외의 문자를 제거한다.
        nickname = nickname.replaceAll("[^가-힣A-Za-z0-9_]", "");

        // 정리한 이름이 비어 있으면 기본 이름을 사용한다.
        if (nickname.isEmpty()) { //위에서 ""받아왔으면
            nickname = "사용자";
        }

        // 뒤에 붙일 밑줄 1자와 랜덤 문자 4자를 위해 최대 15자로 제한한다.
        if (nickname.length() > 15) {
            nickname = nickname.substring(0, 15); //왜 15자냐면 뒤에 _A7K2 이런식으로 붙어야해서
        }

        return nickname;
    }

    // 영문 대문자와 숫자로 랜덤 문자 4자리를 만든다.
    private String generateNicknameSuffix() {
        StringBuilder suffix = new StringBuilder(); //문자를 하나씩 이어 붙일 공간 만듦

        for (int i = 0; i < 4; i++) {
            // 사용할 문자 목록에서 랜덤으로 위치를 고른다. 4번 반복
            int index = secureRandom.nextInt(NICKNAME_CHARACTERS.length());

            // 그 위치의 문자를 하나 붙인다.
            suffix.append(NICKNAME_CHARACTERS.charAt(index));
        }

        return suffix.toString(); //스트링으로 바꿔반환
    }

    // 중복되지 않는 최초 가입용 닉네임을 만든다.
    private String generateUniqueNickname(String socialNickname) {
        // 앞서 만든 메서드로 이름 부분을 정리한다.
        String nicknameBase = prepareNicknameBase(socialNickname);

        // 무한 반복을 피하기 위해 최대 10번 시도한다.
        for (int attempt = 0; attempt < 10; attempt++) {
            // 정리한 이름 + 밑줄 + 랜덤 문자 4자리
            String nickname =
                    nicknameBase + "_" + generateNicknameSuffix();

            // 대소문자를 무시하고 중복 여부를 확인한다.
            if (!userRepository.existsByNicknameIgnoreCase(nickname)) { //중복이 없으면
                return nickname;
            }
        }

        // 열 번 모두 중복이면 생성을 중단한다.
        throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    // 기존 사용자는 반환하고, 처음 로그인한 계정은 가입시킨다.
    @Transactional
    public User findOrCreateUser(
            SocialProvider provider,
            String providerUserId,
            String socialNickname
    ) {
        //여기부터는 위에 만든 것들 실제로 호출해서 사용
        // 기존 연결 조회와 탈퇴 확인
        Optional<User> existingUser =
                findExistingUser(provider, providerUserId);

        // 기존 정상 사용자라면 그대로 반환한다.
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 처음 가입하는 사용자의 닉네임을 만든다.
        String nickname = generateUniqueNickname(socialNickname); // -> prepareNicknameBase() -> generateNicknameSuffix() 호출

        // 부왕 사용자를 DB에 저장한다.
        User newUser = userRepository.save(User.create(nickname));

        // 저장한 부왕 사용자와 소셜 계정을 연결한다.
        SocialAccount socialAccount = SocialAccount.create(
                newUser,
                provider,
                providerUserId
        );
        socialAccountRepository.save(socialAccount);

        return newUser;

    }
    // 소셜 로그인한 회원에게 세션과 부왕 토큰을 발급한다. findOrCreateUser()가 회원을 확인하기에 가입코드는 다시 작성 안함
    @Transactional //중간에 오류발생 시 전부 취소되게끔
    public SocialLoginResult login(
            SocialProvider provider,
            String providerUserId,
            String socialNickname
    ) {
        // 기존 회원을 가져오거나 최초 로그인한 회원을 가입시킨다.
        // 탈퇴한 회원이면 여기서 예외가 발생하여 중단된다.
        User user = findOrCreateUser(
                provider,
                providerUserId,
                socialNickname
        );

        // 토큰 생성과 세션 저장은 공통 토큰 서비스에 맡긴다.
        // 로그인과 재발급이 같은 발급 규칙을 사용한다.
        return authTokenService.issue(user);
    }
}
