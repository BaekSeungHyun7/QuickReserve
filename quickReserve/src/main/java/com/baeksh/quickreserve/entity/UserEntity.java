package com.baeksh.quickreserve.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;


@Entity  // 이 클래스가 JPA 엔티티 -> 데이터베이스 테이블과 매핑 디버깅7
@Data  // Lombok을 사용해 getter, setter, equals, hashCode, toString 메서드 자동 생성
@Builder  // 빌더 패턴으로 객체 생성 지원
@AllArgsConstructor  // 모든 필드를 인자로 받는 생성자 자동 생성
@NoArgsConstructor  // 파라미터가 없는 기본 생성자 자동 생성
public class UserEntity {

    @Id  // 기본 키(primary key) 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 값을 자동으로 생성 하고 자동 증가
    private Long id;  // 사용자 고유 ID, 데이터베이스에서 자동 생성되는 값

    @Column(unique = true)  // 유일한 값을 가지는 컬럼, 중복 X
    private String username;  // 사용자 아이디

    private String password;  // 사용자 비밀번호

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;  // 사용자의 권한 목록, 예: ADMIN, READ, WRITE
}
