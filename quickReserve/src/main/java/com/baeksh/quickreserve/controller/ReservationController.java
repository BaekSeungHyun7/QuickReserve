package com.baeksh.quickreserve.controller;

import com.baeksh.quickreserve.dto.ReservationRequestDto;
import com.baeksh.quickreserve.dto.ReservationDto;
import com.baeksh.quickreserve.service.ReservationService;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import com.baeksh.quickreserve.dto.ReservationCancelRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 진행 API
     * @param request 예약 정보를 담은 DTO (매장 이름과 예약 시간)
     * @return 예약 정보 반환
     */
    @PostMapping("/reservation")
    public ResponseEntity<?> makeReservation(@RequestBody ReservationRequestDto request) {
        // 현재 로그인한 사용자의 username 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();

        // 예약 서비스 호출
        ReservationDto reservation = reservationService.makeReservation(username, request);
        return ResponseEntity.ok(reservation);
    }
    
    /**
     * 예약 취소 API
     * @param request 예약 번호와 취소 사유가 담긴 DTO
     * @return 취소된 예약 정보 반환
     */
    @DeleteMapping("/reservation")
    public ResponseEntity<?> cancelReservation(@RequestBody ReservationCancelRequestDto request) {
        // 현재 로그인한 사용자의 username 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();

        // 예약 취소 서비스 호출
        ReservationDto canceledReservation = reservationService.cancelReservation(username, request);
        return ResponseEntity.ok(canceledReservation);
    }
}
