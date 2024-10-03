package com.baeksh.quickreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private String name;		//매장 이름	
    private String address;		//매장 주소
    private String description; //매장 설명
    private String openingTime; //매장 영업 시작 시간 HHMM
    private String closingTime; //매장 영업 종료 시간 HHMM
}
