package com.baeksh.quickreserve.controller;

import com.baeksh.quickreserve.dto.ReservationRejectRequestDto;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // JSON 변환용 ObjectMapper

    @MockBean
    private ReservationService reservationService;

    @BeforeEach
    public void setup() {

    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"ADMIN"})  // 관리자 권한 설정
    public void testApproveReservation() throws Exception {
        // 예약 승인 테스트
        mockMvc.perform(MockMvcRequestBuilders.put("/reservations/reservation/{reservationId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")) //jwt 토큰 여기
        
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
}

