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
    private String name;
    private String address;
    private String description;
    private String openingTime;
    private String closingTime;
}
