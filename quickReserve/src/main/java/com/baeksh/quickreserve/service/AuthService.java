package com.baeksh.quickreserve.service;

import com.baeksh.quickreserve.dto.AuthDto;
import com.baeksh.quickreserve.entity.UserEntity;
import com.baeksh.quickreserve.repository.UserRepository;
import com.baeksh.quickreserve.exception.CustomException;
import com.baeksh.quickreserve.exception.ErrorCode;
import com.baeksh.quickreserve.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service  // 서비스 레이어
@RequiredArgsConstructor  // 의존성 주입
public class AuthService {

    //리포지토리, 인코더, 토큰 제공자
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원 가입 처리 메서드
     * @param request 회원 가입 요청을 담은 DTO 객체 (아이디, 비밀번호, 권한 목록 포함)
     * @throws CustomException 아이디 중복 시 예외 발생
     */
    public void registerUser(AuthDto.SignUp request) {
        // 주어진 아이디가 이미 존재하는지 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);  // 중복된 아이디 예외 발생
        }

        // 새로운 사용자 객체 생성 및 비밀번호 암호화 후 저장
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))  // 비밀번호를 암호화하여 저장
                .roles(request.getRoles())  // 권한 목록 설정
                .build();

        // DB에 새로운 사용자 저장
        userRepository.save(user);
    }

    /**
     * 사용자 인증 및 JWT 토큰 발급 메서드
     * @param request 로그인 요청을 담은 DTO 객체 (아이디, 비밀번호 포함)
     * @return 인증된 사용자의 JWT 토큰
     * @throws CustomException 아이디가 없거나 비밀번호가 일치하지 않을 경우 예외 발생
     */
    public String authenticateUser(AuthDto.SignIn request) {
        // 주어진 아이디로 사용자를 조회, 없으면 예외 발생
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 입력한 비밀번호와 저장된 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);  // 비밀번호 불일치 예외 발생
        }

        // 인증 성공 시 JWT 토큰을 생성하여 반환
        return jwtTokenProvider.generateToken(user.getUsername(), user.getRoles());
    }
}


