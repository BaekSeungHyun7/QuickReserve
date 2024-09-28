package com.baeksh.quickreserve.dto;

import lombok.Data;

@Data
public class ReservationRequestDto {
    private String restaurantName;  // 예약하려는 매장 이름
    private String reservationTime; // 예약 시간 (두 자리 숫자 형식, 예: "14")
}
