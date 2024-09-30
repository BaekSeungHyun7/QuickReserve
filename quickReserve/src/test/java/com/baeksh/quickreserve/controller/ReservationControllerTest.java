package com.baeksh.quickreserve.controller;

import com.baeksh.quickreserve.dto.ReservationRejectRequestDto;
import com.baeksh.quickreserve.dto.ReservationVisitRequestDto;
import com.baeksh.quickreserve.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.baeksh.quickreserve.dto.ReservationDto; 

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.data.domain.Page; //ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // JSON 변환
    
    private ReservationVisitRequestDto visitRequest;
    private ReservationDto mockReservation;
    @MockBean
    private ReservationService reservationService;

    @BeforeEach
    public void setup() {
        // 테스트용 DTO
        visitRequest = new ReservationVisitRequestDto();
        visitRequest.setUsername("guestUser");
        visitRequest.setReservationId("12345678");
        visitRequest.setRestaurantName("test50");
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"ADMIN"})  // 관리자 권한 설정
    public void testApproveReservation() throws Exception {
        // 예약 승인 테스트
        mockMvc.perform(MockMvcRequestBuilders.put("/reservations/reservation/{reservationId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")) //jwt 토큰
        
        ///eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndWVzdFVzZXIiLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJVU0VSIl0sImlhdCI6MTcyNzYwMzU0NiwiZXhwIjoxNzI3NjA3MTQ2fQ.ghTQ13OU0Nzs0P_s8oHV6ozFF6eU6plk6NweJjOLLYQ
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(1))
                .andExpect(jsonPath("$.username").value("guestUser"))
                .andExpect(jsonPath("$.restaurantName").value("test50"));
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"ADMIN"})  // 관리자 권한 설정
    public void testRejectReservation() throws Exception {
        // 예약 거절 테스트
        ReservationRejectRequestDto rejectRequest = new ReservationRejectRequestDto();
        rejectRequest.setReservationId("1");
        rejectRequest.setRejectReason("Not available");

        mockMvc.perform(MockMvcRequestBuilders.put("/reservations/reservation/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rejectRequest))
                .header("Authorization", "Bearer dummy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(1))
                .andExpect(jsonPath("$.username").value("guestUser"))
                .andExpect(jsonPath("$.restaurantName").value("test50"));
    }
    
    @Test
    @WithMockUser(username = "guestUser", roles = {"USER"})  // 일반 사용자 권한 설정
    public void testVisitReservation() throws Exception {
        // Mock 서비스 동작 설정
        when(reservationService.visitReservation(any(String.class), any(ReservationVisitRequestDto.class)))
                .thenReturn(getMockReservationDto());

        // API 호출 및 테스트
        mockMvc.perform(MockMvcRequestBuilders.put("/reservations/reservation/visit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visitRequest))  // 요청 JSON 변환
                .header("Authorization", "Bearer dummy-jwt-token"))  // JWT 토큰 필요 시 추가
                .andExpect(status().isOk())  // 응답 상태 코드 200 확인
                .andExpect(jsonPath("$.reservationId").value(12345678))  // 예약 번호 확인
                .andExpect(jsonPath("$.username").value("guestUser"))  // 사용자 이름 확인
                .andExpect(jsonPath("$.restaurantName").value("test50"))  // 매장 이름 확인
                .andExpect(jsonPath("$.reservationTime").value("12"));  // 예약 시간 확인
    }

    private ReservationDto getMockReservationDto() {
        // Mock으로 반환
        return ReservationDto.builder()
                .reservationId(12345678L)
                .username("guestUser")
                .restaurantName("test50")
                .reservationTime("12")
                .build();
    }
    
    
    // 1. 예약 번호로 예약 상세 조회 테스트
    @Test
    @WithMockUser(username = "guestUser", roles = {"USER"})  // 회원으로 로그인
    public void testGetReservationDetail() throws Exception {
        // Mock 서비스 동작 설정
        when(reservationService.getReservationDetail(anyString(), anyString())).thenReturn(mockReservation);

        // 예약 상세 조회 API 호출 및 테스트
        mockMvc.perform(MockMvcRequestBuilders.get("/reservations/reservation/search/12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy-jwt-token"))  // JWT 토큰 필요 시 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(12345678))
                .andExpect(jsonPath("$.username").value("guestUser"))
                .andExpect(jsonPath("$.restaurantName").value("test50"))
                .andExpect(jsonPath("$.reservationTime").value("12:00"));
    }

    // 2. 매장 이름으로 예약 목록 조회 테스트 (점장용)
    @Test
    @WithMockUser(username = "managerUser", roles = {"ADMIN"})  // 점장으로 로그인
    public void testGetRestaurantReservations() throws Exception {
        // Mock 서비스 동작 설정
        when(reservationService.getRestaurantReservations(anyString(), anyString(), any())).thenReturn(Page.empty());

        // 예약 목록 조회 API 호출 및 테스트
        mockMvc.perform(MockMvcRequestBuilders.get("/reservations/search/test50")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy-jwt-token"))  // JWT 토큰 필요 시 추가
                .andExpect(status().isOk());
    }

    // 3. 회원의 예약 목록 조회 테스트
    @Test
    @WithMockUser(username = "guestUser", roles = {"USER"})  // 회원으로 로그인
    public void testGetUserReservations() throws Exception {
        // Mock 서비스 동작 설정
        when(reservationService.getUserReservations(anyString(), any())).thenReturn(Page.empty());

        // 회원의 예약 목록 조회 API 호출 및 테스트
        mockMvc.perform(MockMvcRequestBuilders.get("/reservations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy-jwt-token"))  // JWT 토큰 필요 시 추가
                .andExpect(status().isOk());
    }
}

