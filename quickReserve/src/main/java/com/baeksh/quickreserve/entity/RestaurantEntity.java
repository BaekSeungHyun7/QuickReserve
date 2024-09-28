package com.baeksh.quickreserve.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 매장 고유 ID

    @Column(unique = true)
    private String name; // 매장 이름

    private String address; // 매장 주소

    private String description; // 매장 설명

    private String openingTime;

    private String closingTime; // 영업 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner; // 점장
}

