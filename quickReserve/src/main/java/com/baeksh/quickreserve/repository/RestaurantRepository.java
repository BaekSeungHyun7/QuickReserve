package com.baeksh.quickreserve.repository;

import com.baeksh.quickreserve.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    Optional<RestaurantEntity> findByName(String name);
    boolean existsByName(String name);
    
    /**
     * 매장 이름에 검색어를 포함하는 매장 리스트를 반환
     * @param name 검색할 매장 이름
     * @param pageable 페이징 처리 정보
     * @return 페이징된 매장 리스트
     */
    Page<RestaurantEntity> findByNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);

    /**
     * 매장 리스트를 이름순으로 정렬하여 반환
     * @param pageable 페이징 처리 정보
     * @return 페이징된 매장 리스트
     */
    Page<RestaurantEntity> findAllByOrderByNameAsc(Pageable pageable);

}
