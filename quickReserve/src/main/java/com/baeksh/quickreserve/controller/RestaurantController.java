package com.baeksh.quickreserve.controller;

import com.baeksh.quickreserve.dto.RestaurantDto;
import com.baeksh.quickreserve.service.RestaurantService;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import com.baeksh.quickreserve.security.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
	
	//매장 관련 비즈니스 로직을 처리

	private final JwtTokenProvider jwtTokenProvider;
    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(JwtTokenProvider jwtTokenProvider, RestaurantService restaurantService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.restaurantService = restaurantService;
    }
    
    /**
     * 매장 등록 API
     * @param request 매장 등록에 필요한 정보를 담은 DTO
     * @return 성공 시 등록된 매장의 이름을 반환
     */
    @PostMapping("/restaurant")
    public ResponseEntity<?> registerRestaurant(@RequestBody RestaurantDto request) {
        // 현재 로그인한 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 등록 서비스 호출
        String registeredRestaurant = restaurantService.registerRestaurant(request, username);
        return ResponseEntity.ok(registeredRestaurant); // 등록된 매장 이름 반환
    }

    /**
     * 매장 수정 API
     * @param restaurantName 수정할 매장의 기존 이름
     * @param request 수정할 정보를 담은 DTO
     * @return 성공 시 수정된 매장의 이름을 반환
     */
    @PutMapping("/restaurant/{restaurantName}")
    public ResponseEntity<?> updateRestaurant(
            @PathVariable String restaurantName,
            @RequestBody RestaurantDto request) {
        // 현재 로그인한 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 수정 서비스 호출
        String updatedRestaurant = restaurantService.updateRestaurant(restaurantName, request, username);
        return ResponseEntity.ok(updatedRestaurant); // 수정된 매장 이름 반환
    }

    /**
     * 매장 삭제 API
     * @param restaurantName 삭제할 매장 이름
     * @return 성공 시 삭제된 매장의 이름을 반환
     */
    @DeleteMapping("/restaurant/{restaurantName}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable("restaurantName") String restaurantName) {
        // 레스토랑 name 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 삭제 서비스 호출
        String deletedRestaurant = restaurantService.deleteRestaurant(restaurantName, username);
        return ResponseEntity.ok(deletedRestaurant); // 삭제된 매장 이름 반환
    }
    
    /**
     * 매장 검색 API
     * @param query 검색할 매장 이름의 일부 또는 전체
     * @param pageable 페이징 처리
     * @return 검색된 매장의 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRestaurants(
            @RequestParam("query") String query,  // 명시적으로 매개변수 이름 지정
            Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 검색 서비스 호출
        Page<RestaurantDto> restaurantPage = restaurantService.searchRestaurants(query, pageable);
        return ResponseEntity.ok(restaurantPage);
    }
    
    /**
     * 매장 상세 조회 API
     * @param restaurantName 매장 이름
     * @return 매장의 상세 정보
     */
    @GetMapping("/restaurant/{restaurantName}")
    public ResponseEntity<?> getRestaurantDetail(@PathVariable("restaurantName") String restaurantName) {
        if (restaurantName == null || restaurantName.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 상세 정보 조회 서비스 호출
        RestaurantDto restaurant = restaurantService.getRestaurantDetail(restaurantName);
        return ResponseEntity.ok(restaurant);
    }
    
    /**
     * 매장 전체 리스트 조회 API
     * @param pageable 페이징 정보
     * @return 매장의 전체 리스트
     */
    @GetMapping
    public ResponseEntity<?> getAllRestaurants(Pageable pageable) {
        // 매장 리스트 조회 서비스 호출
        Page<RestaurantDto> restaurantPage = restaurantService.getAllRestaurants(pageable);
        return ResponseEntity.ok(restaurantPage);
    }

}

