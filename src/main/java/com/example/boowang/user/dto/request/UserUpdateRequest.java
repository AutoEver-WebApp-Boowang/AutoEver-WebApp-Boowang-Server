package com.example.boowang.user.dto.request;

import jakarta.validation.constraints.Pattern;

// 내 정보 수정에서 허용하는 입력값만 받는다.
public class UserUpdateRequest {

    @Pattern(
            regexp = "^[가-힣A-Za-z0-9_]{2,20}$",
            message = "닉네임은 한글, 영문, 숫자, 밑줄로 2~20자여야 합니다."
    )
    private String nickname;

    @Pattern(
            regexp = "^(010[0-9]{8}|01[16789][0-9]{7,8})$",
            message = "올바른 휴대폰 번호를 입력해 주세요."
    )
    private String phone;

    private boolean nicknameProvided;
    private boolean phoneProvided;

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    // JSON에 nickname이 있으면 호출된다. 공백을 제거한다.
    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.replace(" ", "");
        this.nicknameProvided = true;
    }

    // JSON에 phone이 있으면 호출된다. 하이픈을 제거한다.
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.replace("-", "");
        this.phoneProvided = true;
    }

    public boolean hasNickname() {
        return nicknameProvided;
    }

    public boolean hasPhone() {
        return phoneProvided;
    }
}