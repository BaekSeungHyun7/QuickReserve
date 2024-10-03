package com.baeksh.quickreserve.dto;

import lombok.Data;

import java.util.List;

public class AuthDto {

    @Data  // Lombok을 이용해 getter, setter, toString, equals, hashCode 메서드 자동 생성
    public static class SignUp {
        // 회원 가입 시 필요한 필드들 (아이디, 비밀번호, 권한 목록)
        private String username;  // 사용자 아이디
        private String password;  // 사용자 비밀번호
        private List<String> roles;  // 사용자에게 부여된 역할(권한) 목록 (ADMIN, READ, WRITE)
        private String phoneNumber; //사용자 전화번호
    }

    @Data
    public static class SignIn {
        // 로그인 시 필요한 필드들 (아이디, 비밀번호)
        private String username;  // 로그인 시 입력받는 사용자 아이디
        private String password;  // 로그인 시 입력받는 사용자 비밀번호1
    }
}
