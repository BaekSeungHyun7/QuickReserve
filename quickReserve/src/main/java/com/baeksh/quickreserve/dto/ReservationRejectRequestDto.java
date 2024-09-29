package com.baeksh.quickreserve.dto;

import lombok.Data;

@Data
public class ReservationRejectRequestDto {
    private String reservationId;  // 8자리 숫자 형식의 예약 번호
    private String rejectReason;   // 예약 거절 사유
}
