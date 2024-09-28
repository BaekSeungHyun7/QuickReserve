package com.baeksh.quickreserve.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RestaurantDto {
    private String name;
    private String address;
    private String description;
    private String openingTime;
    private String closingTime;
}
