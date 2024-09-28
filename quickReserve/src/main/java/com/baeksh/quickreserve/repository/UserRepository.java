package com.baeksh.quickreserve.repository;

import com.baeksh.quickreserve.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // 주어진 사용자 아이디로 UserEntity를 조회
    Optional<UserEntity> findByUsername(String username);
    
    // 주어진 사용자 아이디가 이미 존재하는지 여부 확인
    boolean existsByUsername(String username);
}

