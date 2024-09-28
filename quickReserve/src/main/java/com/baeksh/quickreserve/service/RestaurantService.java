package com.baeksh.quickreserve.service;

import com.baeksh.quickreserve.dto.RestaurantDto;
import com.baeksh.quickreserve.entity.RestaurantEntity;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import com.baeksh.quickreserve.repository.RestaurantRepository;
import com.baeksh.quickreserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository; // 매장 정보를 관리하는 Repository
    private final UserRepository userRepository; // 사용자 정보를 관리하는 Repository

    /**
     * 매장 등록 서비스 로직
     * @param request 매장 등록에 필요한 정보가 담긴 DTO
     * @param ownerUsername 매장을 등록하는 사용자의 username
     * @return 등록된 매장의 이름
     */
    @Transactional
    public String registerRestaurant(RestaurantDto request, String ownerUsername) {
        // 매장 이름이 비어있는 경우 예외 처리
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 중복된 매장 이름이 있는지 확인
        if (restaurantRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 매장 소유자 (owner) 찾기
        var owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 매장 엔티티 생성 및 저장
        RestaurantEntity restaurant = RestaurantEntity.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .openingTime(request.getOpeningTime() != null ? request.getOpeningTime() : "00")  // null이면 00
                .closingTime(request.getClosingTime() != null ? request.getClosingTime() : "23")  // null이면 23
                .owner(owner)
                .build();

        restaurantRepository.save(restaurant); // 매장 정보 저장
        return restaurant.getName(); // 등록된 매장 이름 반환
    }

    /**
     * 매장 수정 서비스 로직
     * @param restaurantName 기존 매장 이름 (수정 대상)
     * @param request 수정할 매장 정보가 담긴 DTO
     * @param ownerUsername 매장을 소유한 사용자의 username
     * @return 수정된 매장 이름
     */
    @Transactional
    public String updateRestaurant(String restaurantName, RestaurantDto request, String ownerUsername) {
        // 수정하려는 매장이 존재하는지 확인
        var restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 매장 소유자 확인
        if (!restaurant.getOwner().getUsername().equals(ownerUsername)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 수정하려는 매장의 이름이 다른 매장과 중복되는지 확인
        if (restaurantRepository.existsByName(request.getName()) && !restaurant.getName().equals(request.getName())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 매장 정보 업데이트
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setDescription(request.getDescription());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());

        return restaurant.getName(); // 수정된 매장 이름 반환
    }

    /**
     * 매장 삭제 서비스 로직
     * @param restaurantName 삭제할 매장 이름
     * @param ownerUsername 매장을 소유한 사용자의 username
     * @return 삭제된 매장 이름
     */
    @Transactional
    public String deleteRestaurant(String restaurantName, String ownerUsername) {
        // 삭제하려는 매장이 존재하는지 확인
        var restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 매장 소유자 확인
        if (!restaurant.getOwner().getUsername().equals(ownerUsername)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 삭제
        restaurantRepository.delete(restaurant);
        return restaurant.getName(); // 삭제된 매장 이름 반환
    }
}

