package com.baeksh.quickreserve.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 사용자 고유 ID

    @Column(unique = true)
    private String username;  // 사용자 아이디

    private String password;  // 사용자 비밀번호

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;  // 사용자의 권한 목록, 예: ADMIN, READ, WRITE

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<RestaurantEntity> restaurants;  // 매장과의 연관관계 (1:N)
    
    private String phoneNumber;  // 전화번호
    
    // 매장 소유자인지 확인하는 메서드 추가
    public boolean isOwnerOf(RestaurantEntity restaurant) {
        return this.restaurants.contains(restaurant);
    }
}

