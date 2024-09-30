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
import com.baeksh.quickreserve.dto.ReservationRejectRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baeksh.quickreserve.dto.ReservationVisitRequestDto;

import java.time.Duration;

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
    
    /**
     * 예약 승인 로직
     * @param reservationId 예약 번호
     * @param managerUsername 승인 요청한 매장 관리자의 아이디
     * @return 승인된 예약 정보
     */
    @Transactional
    public ReservationDto approveReservation(String reservationId, String managerUsername) {
        // 예약 번호가 유효한지 확인 (8자리 숫자)
        if (reservationId == null || !reservationId.matches("^\\d{8}$")) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_ID_FORMAT);
        }

        // 예약 확인
        ReservationEntity reservation = reservationRepository.findById(Long.parseLong(reservationId))
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 예약한 매장의 관리자 확인
        RestaurantEntity restaurant = reservation.getRestaurant();
        if (!restaurant.getOwner().getUsername().equals(managerUsername)) {
            throw new CustomException(ErrorCode.NOT_MANAGER);
        }

        // 방문 시간이 지났는지 확인
        if (LocalTime.now().isAfter(reservation.getTime())) {
            reservationRepository.delete(reservation);  // 자동 취소 처리
            throw new CustomException(ErrorCode.RESERVATION_TIME_PASSED);
        }

        // 승인 처리
        reservation.setApproved(true);  // 승인 플래그 설정
        reservationRepository.save(reservation);

        // 승인된 예약 정보 반환
        UserEntity user = reservation.getUser();
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .restaurantName(restaurant.getName())
                .reservationTime(reservation.getTime().toString())
                .build();
    }
    
    /**
     * 예약 거절 로직
     * @param request 예약 번호와 거절 사유가 담긴 DTO
     * @param managerUsername 요청한 매장 관리자의 아이디
     * @return 거절된 예약 정보
     */
    @Transactional
    public ReservationDto rejectReservation(ReservationRejectRequestDto request, String managerUsername) {
        // 예약 번호와 거절 사유가 비어있는지 확인
        if (request.getReservationId() == null || !request.getReservationId().matches("^\\d{8}$") ||
            request.getRejectReason() == null || request.getRejectReason().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 예약 확인
        ReservationEntity reservation = reservationRepository.findById(Long.parseLong(request.getReservationId()))
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 예약한 매장의 관리자 확인
        RestaurantEntity restaurant = reservation.getRestaurant();
        if (!restaurant.getOwner().getUsername().equals(managerUsername)) {
            throw new CustomException(ErrorCode.NOT_MANAGER);
        }

        // 방문 시간이 지났는지 확인
        if (LocalTime.now().isAfter(reservation.getTime())) {
            reservationRepository.delete(reservation);  // 자동 취소 처리
            throw new CustomException(ErrorCode.RESERVATION_TIME_PASSED);
        }

        // 예약 삭제 처리
        reservationRepository.delete(reservation);

        // 거절된 예약 정보 반환
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .username(reservation.getUser().getUsername())
                .phoneNumber(reservation.getUser().getPhoneNumber())
                .restaurantName(restaurant.getName())
                .reservationTime(reservation.getTime().toString())
                .build();
    }
    
    /**
     * 방문 인증 서비스 로직
     * @param loggedInUsername 현재 로그인한 회원 아이디
     * @param request 방문 인증 요청 DTO
     * @return 예약 상세 정보
     */
    @Transactional
    public ReservationDto visitReservation(String loggedInUsername, ReservationVisitRequestDto request) {
        // 1. 예약 번호 형식 확인 (8자리 숫자 문자열)
        if (request.getReservationId() == null || request.getReservationId().length() != 8 || !request.getReservationId().matches("\\d+")) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 2. 회원 아이디, 매장 이름 확인
        if (request.getUsername() == null || request.getUsername().isEmpty() || request.getRestaurantName() == null || request.getRestaurantName().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 3. 예약 번호로 예약 찾기
        ReservationEntity reservation = reservationRepository.findById(Long.parseLong(request.getReservationId()))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 4. 예약한 회원과 현재 로그인한 회원이 같은지 확인
        if (!reservation.getUser().getUsername().equals(loggedInUsername)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }

        // 5. 예약한 매장과 요청 매장이 같은지 확인
        if (!reservation.getRestaurant().getName().equals(request.getRestaurantName())) {
            throw new CustomException(ErrorCode.INVALID_RESTAURANT);
        }

        // 6. 예약 시간이 10분 전에 방문 인증 가능한지 확인
        LocalTime now = LocalTime.now();
        LocalTime reservationTime = reservation.getTime();
        Duration duration = Duration.between(now, reservationTime);

        if (duration.toMinutes() < 0 || duration.toMinutes() > 10) {
            throw new CustomException(ErrorCode.INVALID_VISIT_TIME);
        }

        // 7. 승인된 예약인지 확인
        if (!reservation.isApproved()) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 8. 방문 인증 완료 처리 (예약 상태 업데이트)
        reservation.setVisited(true);
        reservationRepository.save(reservation);

        // 9. 예약 상세 정보 반환
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .username(reservation.getUser().getUsername())
                .restaurantName(reservation.getRestaurant().getName())
                .reservationTime(reservation.getTime().toString())
                .build();
    }
    
    /**
     * 예약 상세 정보 조회 서비스 로직
     * @param username 현재 로그인한 사용자 이름
     * @param reservationNumber 예약 번호
     * @return 예약 상세 정보
     */
    @Transactional
    public ReservationDto getReservationDetail(String username, String reservationNumber) {
        // 예약 번호 확인 (8자리 숫자 형식)
        if (reservationNumber == null || reservationNumber.length() != 8 || !reservationNumber.matches("\\d+")) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 예약 정보 조회
        ReservationEntity reservation = reservationRepository.findById(Long.parseLong(reservationNumber))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 예약한 사용자와 현재 로그인한 사용자가 같은지 확인
        if (!reservation.getUser().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }

        // 예약 정보 반환
        return convertToDto(reservation);
    }

    /**
     * 매장 예약 목록 조회 서비스 로직 (점장용)
     * @param username 점장 아이디
     * @param restaurantName 매장 이름
     * @param pageable 페이징 정보
     * @return 페이징된 예약 목록
     */
    @Transactional
    public Page<ReservationDto> getRestaurantReservations(String username, String restaurantName, Pageable pageable) {
        // 매장 이름 확인
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 매장 정보 조회
        RestaurantEntity restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 현재 사용자가 매장의 관리자(점장)인지 확인
        if (!restaurant.getOwner().getUsername().equals(username)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }

        // 매장의 예약 목록 조회 (예약한 순서대로 정렬)
        Page<ReservationEntity> reservations = reservationRepository.findAllByRestaurantOrderByDateAsc(restaurant, pageable);
        return reservations.map(this::convertToDto);
    }

    /**
     * 회원의 예약 목록 조회 서비스 로직
     * @param username 회원 아이디
     * @param pageable 페이징 정보
     * @return 페이징된 예약 목록
     */
    @Transactional
    public Page<ReservationDto> getUserReservations(String username, Pageable pageable) {
        // 회원 정보 조회
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 회원의 예약 목록 조회
        Page<ReservationEntity> reservations = reservationRepository.findAllByUserOrderByDateAsc(user, pageable);
        return reservations.map(this::convertToDto);
    }

    // ReservationEntity -> ReservationDto 변환 메서드
    private ReservationDto convertToDto(ReservationEntity reservation) {
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .username(reservation.getUser().getUsername())
                .restaurantName(reservation.getRestaurant().getName())
                .reservationTime(reservation.getTime().toString())
                .build();
    }

}
