package com.baeksh.quickreserve.repository;

import com.baeksh.quickreserve.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByRestaurantId(Long restaurantId);  // 매장의 리뷰 목록 조회
}
