package com.baeksh.quickreserve.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode { //Error 코드
    INVALID_INPUT(400, "잘못된 입력 입니다."),
    USERNAME_ALREADY_EXISTS(400, "이미 존재하는 사용자 입니다."),
    USER_NOT_FOUND(400, "사용자를 찾을 수 없습니다."),
    INVALID_MANNGER(400, "매장 소유자가 아닙니다."),
    RESTAURANT_NOT_FOUND(400, "매장을 찾을 수 없습니다."),
    INVALID_PASSWORD(400, "비밀번호가 다릅니다."),
	INTERNAL_SERVER_ERROR(500, "서버 오류"),
	UNAUTHORIZED(401, "로그인이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    RESERVATION_ALREADY_EXISTS(400, "이미 예약이 진행 중입니다."),
    INVALID_RESERVATION_TIME(400, "예약 시간이 유효하지 않습니다."),
    INVALID_RESERVATION_TIME_FORMAT(400, "예약 시간은 두 자리 숫자 형식이어야 합니다."),
    RESERVATION_NOT_FOUND(400, "예약을 찾을 수 없습니다."),
    RESERVATION_OWNER_MISMATCH(400, "예약한 회원이 아닙니다."),
    INVALID_RESERVATION_ID_FORMAT(400, "예약 번호는 8자리 숫자 형식이어야 합니다."),
    RESERVATION_CANNOT_CANCEL_AFTER_ONE_HOUR(400, "예약 시간 1시간 전까지만 취소 가능합니다."),
	NOT_MANAGER(400, "해당 매장의 관리자가 아닙니다."),
    RESERVATION_TIME_PASSED(400, "이미 방문 인증 시간이 지났습니다."),
    INVALID_REJECT_REASON(400, "거절 사유가 잘못되었습니다."),
    RESERVATION_ALREADY_APPROVED(400, "예약이 이미 승인되었습니다."),
    RESERVATION_ALREADY_REJECTED(400, "예약이 이미 거절되었습니다.");
	
    private final int status;
    private final String message;
}
