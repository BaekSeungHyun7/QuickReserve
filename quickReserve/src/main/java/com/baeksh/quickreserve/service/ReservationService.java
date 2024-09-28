package com.baeksh.quickreserve.service;

import com.baeksh.quickreserve.dto.ReservationRequestDto;
import com.baeksh.quickreserve.dto.ReservationDto;
import com.baeksh.quickreserve.entity.ReservationEntity;
import com.baeksh.quickreserve.entity.RestaurantEntity;
import com.baeksh.quickreserve.entity.UserEntity;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import com.baeksh.quickreserve.repository.ReservationRepository;
import com.baeksh.quickreserve.repository.RestaurantRepository;
import com.baeksh.quickreserve.repository.UserRepository;
import com.baeksh.quickreserve.dto.ReservationCancelRequestDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 진행 로직
     * @param username 예약자 이름
     * @param request 예약 정보가 담긴 DTO (매장 이름과 예약 시간)
     * @return 예약 정보 DTO 반환
     */
    @Transactional
    public ReservationDto makeReservation(String username, ReservationRequestDto request) {
    	
        
        // 매장 이름과 예약 시간이 비어 있는지 확인
        if (request.getRestaurantName() == null || request.getRestaurantName().trim().isEmpty() ||
            request.getReservationTime() == null || request.getReservationTime().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 예약 시간이 두 자리 숫자 형식인지 확인
        if (!request.getReservationTime().matches("^\\d{2}$")) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME_FORMAT);
        }

        // 회원 확인
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 매장 확인
        RestaurantEntity restaurant = restaurantRepository.findByName(request.getRestaurantName())
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 이미 같은 매장에 예약이 있는지 확인
        if (reservationRepository.existsByUserAndRestaurantAndDate(user, restaurant, LocalDate.now())) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_EXISTS);
        }

        // 현재 시간과 예약 시간이 유효한지 확인
        LocalTime now = LocalTime.now();
        LocalTime reservationTime = LocalTime.of(Integer.parseInt(request.getReservationTime()), 0);
        if (reservationTime.isBefore(now) || reservationTime.isBefore(LocalTime.parse(restaurant.getOpeningTime())) ||
            reservationTime.isAfter(LocalTime.parse(restaurant.getClosingTime()))) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }

        // 예약 엔티티 생성 및 저장
        ReservationEntity reservation = ReservationEntity.builder()
                .user(user)
                .restaurant(restaurant)
                .date(LocalDate.now())
                .time(LocalTime.of(Integer.parseInt(request.getReservationTime()), 0))
                .build();

        reservationRepository.save(reservation);

        // 예약 정보 반환
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .username(username)
                .phoneNumber(user.getPhoneNumber())
                .restaurantName(restaurant.getName())
                .reservationTime(reservationTime.toString())
                .build();
    }
    
    /**
     * 예약 취소 로직
     * @param username 예약자 이름
     * @param request 취소할 예약 번호와 사유가 담긴 DTO
     * @return 취소된 예약 정보 반환
     */
    @Transactional
    public ReservationDto cancelReservation(String username, ReservationCancelRequestDto request) {
        // 예약 번호와 취소 사유가 비어 있는지 확인
        if (request.getReservationId() == null || request.getReservationId().trim().isEmpty() ||
            request.getCancelReason() == null || request.getCancelReason().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 예약 번호가 8자리 숫자 형식인지 확인
        if (!request.getReservationId().matches("^\\d{8}$")) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_ID_FORMAT);
        }

        // 예약 확인
        ReservationEntity reservation = reservationRepository.findById(Long.parseLong(request.getReservationId()))
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 예약한 회원과 요청한 회원이 동일한지 확인
        if (!reservation.getUser().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.RESERVATION_OWNER_MISMATCH);
        }

        // 예약 시간 1시간 전까지 취소 가능 확인
        LocalTime now = LocalTime.now();
        if (now.plusHours(1).isAfter(reservation.getTime())) {
            throw new CustomException(ErrorCode.RESERVATION_CANNOT_CANCEL_AFTER_ONE_HOUR);
        }

        // 예약 취소 처리
        reservationRepository.delete(reservation);

        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .username(username)
                .restaurantName(reservation.getRestaurant().getName())
                .reservationTime(reservation.getTime().toString())
                .build();
    }

}
