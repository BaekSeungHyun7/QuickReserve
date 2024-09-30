package com.baeksh.quickreserve.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationVisitRequestDto {
    private String username;       // 회원 아이디
    private String reservationId;  // 예약 번호 (8자리 숫자)
    private String restaurantName; // 매장 이름
}
