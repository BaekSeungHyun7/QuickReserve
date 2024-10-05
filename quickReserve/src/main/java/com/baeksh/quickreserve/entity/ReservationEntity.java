package com.baeksh.quickreserve.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

	//~241004 0731
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값이 자동으로 생성되도록 설정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // N:1 관계에서 예약을 한 사용자와의 관계
    @JoinColumn(name = "user_id")  // 외래 키(FK)로 user_id 컬럼 사용
    private UserEntity user;  // 예약을 한 사용자

    @ManyToOne(fetch = FetchType.LAZY)  // N:1 관계에서 예약된 매장과의 관계
    @JoinColumn(name = "restaurant_id")  // 외래 키(FK)로 restaurant_id 컬럼 사용
    private RestaurantEntity restaurant;  // 예약된 매장

    private LocalDate date;         // 예약 날짜
    private LocalTime time;         // 예약 시간
    
    private boolean approved; //예약 승인 여부
    
    private boolean visited;  // 방문 인증 여부
    
}

