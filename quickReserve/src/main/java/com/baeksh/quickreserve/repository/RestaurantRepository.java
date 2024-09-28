package com.baeksh.quickreserve.repository;

import com.baeksh.quickreserve.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    Optional<RestaurantEntity> findByName(String name);
    boolean existsByName(String name);
}
