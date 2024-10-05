package com.baeksh.quickreserve.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // N:1 관계에서 리뷰 작성자와의 관계
    @JoinColumn(name = "user_id")  // 외래 키(FK)로 user_id 컬럼 사용
    private UserEntity user;  // 리뷰 작성자

    @ManyToOne(fetch = FetchType.LAZY)  // N:1 관계에서 리뷰 대상 매장과의 관계
    @JoinColumn(name = "restaurant_id")  // 외래 키(FK)로 restaurant_id 컬럼 사용
    private RestaurantEntity restaurant;  // 리뷰 대상 매장
    
    private String title;  // 리뷰 제목
    private String content;  // 리뷰 내용
}
