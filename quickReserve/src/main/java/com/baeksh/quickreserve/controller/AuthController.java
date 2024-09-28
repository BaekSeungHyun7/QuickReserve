package com.baeksh.quickreserve.controller;

import com.baeksh.quickreserve.dto.AuthDto;
import com.baeksh.quickreserve.service.AuthService;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    //회원 가입 및 로그인 로직을 처리
    private final AuthService authService;

    /**
     * 회원 가입 API -> 엔드포인트.
     * @param request 회원 가입 요청에 포함된 아이디, 비밀번호, 권한 정보를 담고 있는 DTO 객체.
     * @return 성공 시, 가입된 사용자의 아이디를 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthDto.SignUp request) {
        // 아이디, 비밀번호, 권한이 null이거나 비어 있으면 예외처리
        if (request.getUsername() == null || request.getPassword() == null || request.getRoles().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        //AuthService로 전달하여 회원을 등록
        authService.registerUser(request);

        //해당 사용자의 아이디를 반환
        return ResponseEntity.ok(request.getUsername());
    }

    /**
     * 로그인 API -> 엔드포인트.
     * @param request 로그인 요청에 포함된 아이디와 비밀번호를 담고 있는 DTO 객체.
     * @return 성공 시, JWT 토큰을 반환합니다.
     */
    
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AuthDto.SignIn request) {
        // 아이디와 비밀번호가 null 이면 예외처리
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 인증 성공 시 JWT 토큰을 생성하여 반환
        String token = authService.authenticateUser(request);

        //JWT 토큰을 반환
        return ResponseEntity.ok(token);
    }
}


