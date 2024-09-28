package com.baeksh.quickreserve.dto;

import lombok.Data;

@Data
public class ReservationCancelRequestDto {
    private String reservationId;  // 8자리 숫자 형식의 예약 번호
    private String cancelReason;   // 취소 사유
}
