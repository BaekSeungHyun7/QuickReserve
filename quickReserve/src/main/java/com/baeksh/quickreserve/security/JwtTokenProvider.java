package com.baeksh.quickreserve.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component  // 스피링 bean
public class JwtTokenProvider {

    @Value("${jwt.secret}")  // application.properties 정의된 jwt.secret 값으로 설정(실제 환경에서는 외부에서)
    private String secretKey;  // JWT 서명에 사용할 시크릿 키

    // JWT 토큰의 만료 시간 설정 (1시간)
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour

    /**
     * JWT 토큰 생성 메서드
     * @param username 사용자 이름
     * @param roles 사용자 권한 목록 (ADMIN, READ, WRITE 등)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String username, List<String> roles) {
        // 클레임에 사용자 이름과 권한을 추가
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        // 현재 시간과 만료 시간
        Date now = new Date();
        Date validity = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        // JWT 토큰을 생성하고 서명 알고리즘과 시크릿 키를 사용해 서명
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)  // SHA-256
                .compact();  //토큰 문자열 반환
    }
}


