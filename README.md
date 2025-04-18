"# SubWeb_ver1_local" 

✅ 1. 프로젝트 구조
## 📦 프로젝트 구조

```
SubWeb_ver1_local/
├── backend/           # Spring Boot (JWT + OAuth2 + CSRF + REST API)
├── frontend/          # React (Axios + JWT Cookie + CSRF 연동)
├── nginx/             # Nginx 리버스 프록시 + HTTPS 인증서
├── docker-compose.yml # 전체 앱을 컨테이너로 구성
└── README.md
```

✅ 2. 기능 요약 표
## 🔐 주요 기능

| 기능 항목                | 설명                                       |
|--------------------------|--------------------------------------------|
| ✅ JWT 인증               | Access + Refresh Token, 쿠키 기반 저장      |
| 🔐 OAuth2 로그인         | Google, Kakao, GitHub 등 외부 인증 지원     |
| 🛡️ CSRF 보호              | XSRF-TOKEN + 헤더 기반 토큰 검증           |
| 🔒 쿠키 설정              | Secure, HttpOnly, SameSite=None 적용       |
| 🌐 HTTPS 구성             | Nginx + 인증서 (pem/key)                   |
| 🐳 Docker 컨테이너 구성  | docker-compose 로 통합 관리                |

✅ 3. 서비스 경로
## 🌍 접근 경로

| 서비스 유형     | 주소                                |
|------------------|-------------------------------------|
| 프론트엔드 앱    | `https://localhost`                 |
| 백엔드 API       | `https://localhost/api/**`          |
| OAuth2 콜백      | `https://localhost/login/oauth2/**` |

🧪 인증 흐름 요약
OAuth2 로그인 성공 → JWT + RefreshToken Secure 쿠키로 발급

이후 모든 요청에 쿠키 기반 인증 적용

POST 요청 시 XSRF-TOKEN + X-XSRF-TOKEN 헤더로 CSRF 방어

로그아웃 시 쿠키(JWT, Refresh, CSRF, JSESSIONID) 모두 제거

✅ 4. 데이터베이스 정보
MySQL
## 🗃️ 데이터베이스 테이블 구조 요약

### 📌 `account` (회원 정보)

| 컬럼명      | 타입         | 제약조건        | 설명           |
|-------------|--------------|-----------------|----------------|
| account_id  | int          | PK, AUTO_INCREMENT | 사용자 고유 ID |
| name        | varchar(45)  | NOT NULL, UNIQUE   | 사용자 이름     |
| pwd         | varchar(200) | NOT NULL           | 비밀번호 (암호화 저장) |
| email       | varchar(100) | NOT NULL           | 이메일 주소     |
| create_dt   | date         | NOT NULL           | 생성 일자       |

---

### 📌 `role` (권한 정보)

| 컬럼명      | 타입         | 제약조건        | 설명           |
|-------------|--------------|-----------------|----------------|
| role_id     | int          | PK, AUTO_INCREMENT | 권한 ID     |
| role_name   | varchar(45)  | NOT NULL           | 예: `ROLE_USER`, `ROLE_ADMIN` |
| account_id  | int          | FK → account       | 사용자 연관     |

---

### 📌 `friend_list` (친구 관계)

| 컬럼명           | 타입     | 제약조건                | 설명                     |
|------------------|----------|--------------------------|--------------------------|
| friend_list_id    | int     | PK, AUTO_INCREMENT       | 친구 리스트 ID           |
| my_account_id     | int     | FK → account             | 나                      |
| friend_account_id | int     | FK → account             | 친구                    |
| create_dt         | date    | NOT NULL                 | 친구 등록일              |

---

### 📌 `subculture` (서브컬처 컨텐츠)

| 컬럼명       | 타입         | 제약조건        | 설명              |
|--------------|--------------|-----------------|-------------------|
| subculture_id| int          | PK, AUTO_INCREMENT | 콘텐츠 고유 ID   |
| title        | varchar(100) | NOT NULL, UNIQUE   | 제목             |
| genre        | varchar(45)  | NOT NULL           | 장르             |
| image_path   | varchar(1000)| NULL 가능          | 이미지 경로       |
| create_dt    | date         | NOT NULL           | 등록 일자         |

---

### 📌 `post` (게시글)

| 컬럼명       | 타입         | 제약조건        | 설명              |
|--------------|--------------|-----------------|-------------------|
| post_id      | int          | PK, AUTO_INCREMENT | 게시글 ID       |
| title        | varchar(45)  | NOT NULL           | 제목             |
| post_body    | mediumtext   | NULL 가능          | 본문             |
| subculture_id| int          | FK → subculture     | 서브컬처 참조     |
| account_id   | int          | FK → account        | 작성자 (NULL 가능) |
| is_notice    | tinyint      | NOT NULL, 기본값 0   | 공지 여부         |
| create_dt    | date         | NOT NULL           | 작성 일자         |

---

### 📌 `message` (쪽지/요청)

| 컬럼명       | 타입         | 제약조건        | 설명              |
|--------------|--------------|-----------------|-------------------|
| id           | int          | PK, AUTO_INCREMENT | 메시지 ID       |
| send_id      | int          | FK → account        | 보낸 사람         |
| receive_id   | int          | FK → account        | 받는 사람         |
| is_notice    | tinyint      | NOT NULL            | 공지성 여부        |
| is_request   | tinyint      | NOT NULL            | 친구 요청 여부     |
| message_body | mediumtext   | NOT NULL            | 내용              |
| create_dt    | date         | NOT NULL            | 보낸 날짜          |


✅ 5. 개발자 정보
## ✍️ 개발자

| 이름   | 역할                                  |
|--------|---------------------------------------|
| Kcjkcj | 백엔드 + 프론트엔드 인증 구조 설계 및 개발 |

<h3>Velog 글</h3>
https://velog.io/@backnewbie/-%EC%9B%B9-%EC%96%B4%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%EA%B0%9C%EB%B0%9C%EC%9D%BC%EC%A7%80-%EC%9D%BC%EA%B3%B1-%EB%B2%88%EC%A7%B8

