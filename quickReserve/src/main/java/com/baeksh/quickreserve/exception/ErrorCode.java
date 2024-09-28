package com.baeksh.quickreserve.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode { //Error 코드
    INVALID_INPUT(400, "잘못된 입력 입니다."),
    USERNAME_ALREADY_EXISTS(400, "이미 존재하는 사용자 입니다."),
    USER_NOT_FOUND(400, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(400, "비밀번호가 다릅니다."),
	 INTERNAL_SERVER_ERROR(500, "서버 오류");
	
    private final int status;
    private final String message;
}
