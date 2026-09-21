package com.example.boowang.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name= "users") //테이블 이름 users
@Getter
//JPA가 사용할 빈 생성자를 만든다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) //아무 곳에서나 의미 없는 빈 사용자를 만들지 못하게 한다.
public class User {

    @Id// 기본키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private int trustScore;

    @CreationTimestamp //회원이 처음 저장될 때 자동 기록
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)//회원 정보가 수정될 때 자동 갱신
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt; // 탈퇴할 때 우리가 직접 시간을 넣을 것임 가입하자마자 탈퇴한 것으로 처리되면 안 되기 때문

    // 처음 로그인한 사용자를 만든다.
    private User(String nickname) {
        this.nickname = nickname;
        this.trustScore = 0;
    }
    public void increaseTrustScore() {
        this.trustScore += 20;
    }

    // 다른 클래스에서는 이 메서드를 사용해서 새 사용자를 만든다.
    public static User create(String nickname) {
        return new User(nickname);
    }


    // 사용자의 닉네임을 변경한다.
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    // 휴대폰 번호를 변경한다. null이면 기존 번호를 삭제한다.
    public void changePhone(String phone) {
        this.phone = phone;
    }

    // 회원을 실제로 삭제하지 않고 탈퇴 시각을 기록한다.
    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
    }
}
