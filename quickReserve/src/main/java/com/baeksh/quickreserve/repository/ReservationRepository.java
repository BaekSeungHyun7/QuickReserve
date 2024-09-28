package com.baeksh.quickreserve.repository;

import com.baeksh.quickreserve.entity.ReservationEntity;
import com.baeksh.quickreserve.entity.RestaurantEntity;
import com.baeksh.quickreserve.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    // 동일한 회원과 매장에 같은 날 예약이 있는지 확인
    boolean existsByUserAndRestaurantAndDate(UserEntity user, RestaurantEntity restaurant, LocalDate date);
}
