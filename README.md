# **매장 예약 및 리뷰 시스템**

## **기술 스택**
- **Java 11+**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **JPA**
- **MySQL**
- **Gradle**
- **JUnit 5 (테스트)**

---

## **프로젝트 개요**
이 프로젝트는 사용자가 매장을 예약하고, 매장 이용 후 리뷰를 남길 수 있는 **매장 예약 및 리뷰 시스템**입니다. 주요 기능은 다음과 같습니다:

- **매장 예약**: 고객은 매장을 검색하고, 예약할 수 있습니다. 예약 후 매장 관리자가 예약을 승인하거나 거절할 수 있습니다.
- **매장 관리**: 매장 관리자는 매장을 등록하고, 예약을 관리하며, 리뷰를 삭제할 수 있습니다.
- **JWT 기반 인증**: **JWT**를 이용하여 안전한 로그인과 인증을 구현했으며, **USER**(일반 사용자), **ADMIN**(매장 관리자)와 같은 권한을 기반으로 기능을 제한할 수 있습니다.
- **리뷰 작성 및 관리**: 고객은 매장 이용 후 리뷰를 작성하고, 수정 및 삭제할 수 있으며, 매장 관리자는 리뷰를 삭제할 권한을 가집니다.

---

## **API 명세**

### **1. 회원 관리 API**
#### 1.1 회원 가입
- **POST /auth/signup**: 사용자 등록을 위한 API입니다.
  - `username`, `password`, `roles`(권한 목록)
  - **응답**: 성공 시 회원의 `username`을 반환합니다.

#### 1.2 로그인
- **POST /auth/signin**: 로그인 시 사용자 인증을 위한 API입니다.
  - `username`, `password`
  - **응답**: 성공 시 JWT 토큰을 반환합니다.

---

### **2. 매장 관리 API**
#### 2.1 매장 등록
- **POST /restaurants/restaurant**: 매장 관리자가 매장을 등록하는 API입니다.
  - `name`, `address`, `description`, `openingTime`, `closingTime`
  - **응답**: 등록된 매장의 이름을 반환합니다.

#### 2.2 매장 수정
- **PUT /restaurants/restaurant/{restaurantName}**: 매장 관리자가 매장 정보를 수정하는 API입니다.
  - `name`, `address`, `description`, `openingTime`, `closingTime`
  - **응답**: 수정된 매장의 이름을 반환합니다.

#### 2.3 매장 삭제
- **DELETE /restaurants/restaurant/{restaurantName}**: 매장 관리자가 매장을 삭제하는 API입니다.
  - **응답**: 삭제된 매장의 이름을 반환합니다.

#### 2.4 매장 검색
- **GET /restaurants/search**: 매장 이름으로 매장을 검색하는 API입니다.
   `query` (검색어)
  - **응답**: 검색된 매장 리스트를 반환합니다.

#### 2.5 매장 상세 조회
- **GET /restaurants/restaurant/{restaurantName}**: 매장 이름을 통해 매장 상세 정보를 조회하는 API입니다.
  - **응답**: 해당 매장의 상세 정보를 반환합니다.

---

### **3. 예약 관리 API**
#### 3.1 예약 진행
- **POST /reservations/reservation**: 회원이 매장을 예약하는 API입니다.
  - `restaurantName`, `reservationTime`
  - **응답**: 예약 번호, 예약한 회원의 아이디, 매장 이름, 예약 시간을 반환합니다.

#### 3.2 예약 취소
- **DELETE /reservations/reservation**: 회원이 예약을 취소하는 API입니다.
  - `reservationId`, `cancellationReason`(취소 사유)
  - **응답**: 취소된 예약 번호를 반환합니다.

#### 3.3 예약 승인
- **PUT /reservations/reservation/{reservationId}**: 매장 관리자가 예약을 승인하는 API입니다.
  - **응답**: 승인된 예약 정보를 반환합니다.

#### 3.4 예약 거절
- **PUT /reservations/reservation/reject**: 매장 관리자가 예약을 거절하는 API입니다.
  - `reservationId`, `rejectionReason`(거절 사유)
  - **응답**: 거절된 예약 정보를 반환합니다.

#### 3.5 예약 조회 (회원)
- **GET /reservations/search**: 회원이 자신의 예약 목록을 조회하는 API입니다.
  - **응답**: 회원의 예약 리스트를 반환합니다.

#### 3.6 예약 조회 (매장 관리자)
- **GET /reservations/search/{restaurantName}**: 매장 관리자가 해당 매장의 예약 목록을 조회하는 API입니다.
  - **응답**: 매장의 예약 리스트를 반환합니다.

#### 3.7 예약 상세 조회
- **GET /reservations/reservation/search/{reservationNumber}**: 예약 번호로 예약 상세 정보를 조회하는 API입니다.
  - **응답**: 예약 상세 정보를 반환합니다.

---

### **4. 리뷰 관리 API**
#### 4.1 리뷰 작성
- **POST /reviews/review**: 회원이 매장에 대한 리뷰를 작성하는 API입니다.
  - `restaurantName`, `title`, `content`
  - **응답**: 작성된 리뷰의 ID, 제목, 내용을 반환합니다.

#### 4.2 리뷰 수정
- **PUT /reviews/review/{id}**: 회원이 작성한 리뷰를 수정하는 API입니다.
  - `title`, `content`
  - **응답**: 수정된 리뷰 정보를 반환합니다.

#### 4.3 리뷰 삭제
- **DELETE /reviews/review/{id}**: 회원 또는 매장 관리자가 리뷰를 삭제하는 API입니다.
  - **응답**: 삭제된 리뷰의 ID를 반환합니다.

#### 4.4 리뷰 상세 조회
- **GET /reviews/search/{id}**: 리뷰의 ID로 리뷰 상세 정보를 조회하는 API입니다.
  - **응답**: 해당 리뷰의 상세 정보를 반환합니다.

#### 4.5 리뷰 전체 리스트 조회
- **GET /reviews/search**: 작성된 모든 리뷰 목록을 조회하는 API입니다.
  - **응답**: 전체 리뷰 리스트를 반환합니다.

---

## **에러 처리**
- **400 Bad Request**: 잘못된 요청 파라미터 또는 필수 데이터 누락 시 반환.
- **401 Unauthorized**: 인증되지 않은 사용자일 경우 반환.
- **403 Forbidden**: 권한이 없는 사용자일 경우 반환.
- **404 Not Found**: 요청한 리소스가 존재하지 않을 경우 반환.
