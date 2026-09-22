# Boowang Server

사용자가 장소를 등록하고 리뷰, 좋아요, 즐겨찾기와 신뢰도 정보를 관리할 수 있는 Boowang 서비스의 Spring Boot 백엔드입니다.

## 배포 주소

- 웹 서비스(Vercel): [https://auto-ever-web-app-boowang-web.vercel.app](https://auto-ever-web-app-boowang-web.vercel.app/)
- 백엔드 API: [https://boowang.duckdns.org](https://boowang.duckdns.org/)
- Swagger UI: [https://boowang.duckdns.org/swagger-ui/index.html](https://boowang.duckdns.org/swagger-ui/index.html)
- OpenAPI JSON: [https://boowang.duckdns.org/v3/api-docs](https://boowang.duckdns.org/v3/api-docs)

## 주요 기능

- 카카오·현대자동차 OAuth 2.0 로그인 및 최초 로그인 시 자동 회원가입
- JWT Access Token 인증
- Refresh Token 회전, 로그아웃 및 다중 로그인 세션 관리
- 내 정보 조회·수정·탈퇴와 즐겨찾기 목록 조회
- 장소 등록·조회·검색·수정·삭제
- 장소 사진 업로드 및 AWS S3 저장
- 장소 반응, 즐겨찾기, 리뷰와 리뷰 좋아요
- Swagger UI를 통한 API 확인

## 기술 스택

- Java 17
- Spring Boot 4.1.1
- Spring Security, OAuth 2.0 Client
- Spring Data JPA, MySQL 8
- JWT (`jjwt`)
- AWS S3 SDK
- Gradle
- Docker, Docker Compose

## 프로젝트 구조

```text
src/main/java/com/example/boowang
├── auth      # 소셜 로그인, 토큰과 인증 세션
├── global    # 보안, 예외, 공통 응답, CORS, S3 설정
├── place     # 장소, 사진, 반응과 즐겨찾기
├── review    # 리뷰와 리뷰 좋아요
└── user      # 사용자 프로필, 탈퇴와 즐겨찾기 조회
```

## 실행 준비

### 요구 사항

- JDK 17
- MySQL 8.0
- OAuth 애플리케이션의 Client ID와 Client Secret
- 사진 업로드 기능을 위한 AWS S3 자격 증명

### 환경 변수

프로젝트 루트의 `.env` 파일은 Git에 포함하지 않습니다. 아래 항목을 로컬 환경, IDE 실행 설정 또는 배포 환경에 등록해야 합니다.

```dotenv
DB_URL=jdbc:mysql://localhost:3306/boowang_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your_password

JWT_SECRET=your_base64_encoded_secret
JWT_ACCESS_TOKEN_EXPIRATION_MS=3600000
JWT_REFRESH_TOKEN_EXPIRATION_MS=1209600000

KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
HYUNDAI_CLIENT_ID=your_hyundai_client_id
HYUNDAI_CLIENT_SECRET=your_hyundai_client_secret

AWS_S3_BUCKET=your_bucket_name
AWS_ACCESS_KEY=your_access_key
AWS_SECRET_KEY=your_secret_key

APP_AUTH_COOKIE_SECURE=false
APP_AUTH_COOKIE_SAME_SITE=Lax
APP_AUTH_LOGIN_SUCCESS_URL=http://localhost:5173/oauth/callback
APP_CORS_ALLOWED_ORIGIN=http://localhost:5173
```

운영 환경에서는 HTTPS를 사용하고 `APP_AUTH_COOKIE_SECURE=true`, `APP_AUTH_COOKIE_SAME_SITE=None`으로 설정합니다.

## 로컬 실행

MySQL만 Docker로 실행하려면 다음 명령을 사용합니다.

```bash
docker compose up -d mysql
```

환경 변수를 등록한 뒤 애플리케이션을 실행합니다.

### Windows

```powershell
.\gradlew.bat bootRun
```

### macOS / Linux

```bash
./gradlew bootRun
```

기본 서버 주소는 `http://localhost:8080`입니다.

## API 문서

애플리케이션 실행 후 Swagger UI에서 전체 요청·응답 형식을 확인할 수 있습니다.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 인증

### 소셜 로그인 시작 주소

- 카카오: `GET /oauth2/authorization/kakao`
- 현대자동차: `GET /oauth2/authorization/hyundai`

소셜 인증에 성공하면 서버가 Refresh Token을 HttpOnly 쿠키로 저장하고 `APP_AUTH_LOGIN_SUCCESS_URL`로 이동시킵니다. 프론트엔드는 이후 토큰 재발급 API를 호출하여 Access Token을 받아 사용합니다.

보호된 API를 호출할 때는 다음 헤더를 전송합니다.

```http
Authorization: Bearer {access-token}
```

### 인증 API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/api/v1/auth/refresh` | Refresh Token으로 토큰 재발급 |
| `POST` | `/api/v1/auth/logout` | 현재 로그인 세션 종료 |
| `POST` | `/api/test-auth/login` | 개발용 테스트 사용자 로그인 |
| `GET` | `/api/test-auth/me` | JWT 인증 동작 확인 |

## 주요 API

| 영역 | Method | Endpoint | 설명 |
| --- | --- | --- | --- |
| 사용자 | `GET` | `/api/v1/users/me` | 내 정보 조회 |
| 사용자 | `PATCH` | `/api/v1/users/me` | 내 정보 수정 |
| 사용자 | `DELETE` | `/api/v1/users/me` | 회원 탈퇴 |
| 사용자 | `GET` | `/api/v1/users/me/favorites` | 내 즐겨찾기 조회 |
| 장소 | `GET` | `/api/places` | 장소 목록 조회 |
| 장소 | `GET` | `/api/places/search` | 장소 검색 |
| 장소 | `GET` | `/api/places/{placeId}` | 장소 상세 조회 |
| 장소 | `POST` | `/api/places` | 장소 등록 |
| 장소 | `PATCH` | `/api/places/{placeId}` | 장소 수정 |
| 장소 | `DELETE` | `/api/places/{placeId}` | 장소 삭제 |
| 즐겨찾기 | `POST` | `/api/places/{placeId}/favorites` | 즐겨찾기 추가 |
| 즐겨찾기 | `DELETE` | `/api/places/{placeId}/favorites` | 즐겨찾기 삭제 |
| 사진 | `POST` | `/api/places/{placeId}/photos` | 장소 사진 업로드 |
| 반응 | `POST` | `/api/places/{placeId}/reactions` | 장소 반응 등록 |
| 반응 | `DELETE` | `/api/places/{placeId}/reactions` | 장소 반응 삭제 |
| 리뷰 | `GET` | `/api/places/{placeId}/reviews` | 리뷰 목록 조회 |
| 리뷰 | `POST` | `/api/places/{placeId}/reviews` | 리뷰 작성 |
| 리뷰 | `POST` | `/api/reviews/{reviewId}/likes` | 리뷰 좋아요 |
| 리뷰 | `DELETE` | `/api/reviews/{reviewId}/likes` | 리뷰 좋아요 취소 |

장소와 리뷰 조회 API 일부를 제외한 API는 Access Token이 필요합니다.

## 빌드 및 테스트

### Windows

```powershell
.\gradlew.bat test
.\gradlew.bat bootJar
```

### macOS / Linux

```bash
./gradlew test
./gradlew bootJar
```

빌드 결과물은 `build/libs/boowang-0.0.1-SNAPSHOT.jar`에 생성됩니다.

## Docker 이미지 빌드

```bash
docker build -t boowang-server .
docker run --env-file .env -p 8080:8080 boowang-server
```

`docker-compose.yml`의 `app` 서비스는 로컬 소스가 아니라 지정된 원격 이미지를 실행하므로, 로컬 변경 사항을 확인할 때는 Gradle로 실행하거나 이미지를 직접 빌드하세요.
