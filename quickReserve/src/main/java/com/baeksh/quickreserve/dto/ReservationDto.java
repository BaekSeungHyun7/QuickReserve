package com.baeksh.quickreserve.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationDto {
    private Long reservationId;      // 예약 번호
    private String username;         // 예약한 회원 아이디
    private String restaurantName;   // 예약한 매장 이름
    private String reservationTime;  // 예약 시간
    private String phoneNumber;      // 회원 전화번호
}
