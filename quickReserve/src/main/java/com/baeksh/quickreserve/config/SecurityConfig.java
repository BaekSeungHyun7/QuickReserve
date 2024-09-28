package com.baeksh.quickreserve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

	@Bean  // 의존성 주입
    public PasswordEncoder passwordEncoder() {// 비밀번호를 해싱(암호화)
        return new BCryptPasswordEncoder(); // BCrypt
    }
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // API 테스트용 CSRF 보호 비활성화 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/signup", "/auth/signin").permitAll()  // 회원가입과 로그인 경로는 인증 없이 허용
                .anyRequest().permitAll()  // 모든 요청 허용
            )
            .formLogin().permitAll();

        return http.build();
    }
}
