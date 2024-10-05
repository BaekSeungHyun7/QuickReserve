package com.baeksh.quickreserve.service;

import com.baeksh.quickreserve.repository.UserRepository;
import com.baeksh.quickreserve.entity.UserEntity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;  // 사용자 데이터를 처리하는 UserRepository

    /**
     * 생성자 - UserRepository 주입
     * @param userRepository 사용자 저장소
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 주어진 사용자 이름으로 사용자 정보를 로드
     * @param username 사용자 이름 (로그인 ID)
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자가 없는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이름으로 사용자 조회, 없으면 예외 발생
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 조회된 사용자를 UserDetails 객체로 변환
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().toArray(new String[0]))  // 권한 설정
                .accountExpired(false)  // 계정 만료 여부 설정
                .accountLocked(false)  // 계정 잠금 여부 설정
                .credentialsExpired(false)  // 자격 증명 만료 여부 설정
                .disabled(false)  // 계정 비활성화 여부 설정
                .build();  // UserDetails 객체 생성
    }
}