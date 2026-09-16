package com.example.boowang.user.repository;

import com.example.boowang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 탈퇴하지 않은 사용자를 ID로 조회한다 >탈퇴한 사용자는 deletedAt에 시간이 들어가므로 조회되지 않는다.
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    // 탈퇴하지 않은 사용자를 닉네임으로 조회한다. >이부분은 테스트사용자 용
    Optional<User> findByNicknameIgnoreCaseAndDeletedAtIsNull(String nickname);

    // 최초 닉네임을 만들 때 중복 여부를 확인한다.
    boolean existsByNicknameIgnoreCase(String nickname);

    // 내 정보 수정 시 자기 자신을 제외하고 닉네임 중복을 확인한다 >나와 같은 id는 제외하고 다른사용자가 사용하는지만 검사
    boolean existsByNicknameIgnoreCaseAndIdNot(String nickname, Long id);


}