# AuthSpring

Spring Boot 기반 **인증 및 사용자 관리 REST API 서비스** 프로젝트입니다.
JWT 기반 인증과 MySQL 데이터베이스 연동을 지원하며, Swagger UI를 통해 API 명세를 확인할 수 있습니다.

---

## 🛠 기술 스택

* **Backend**: Java 17, Spring Boot, Spring Data JPA, HikariCP
* **Database**: MySQL 8
* **Security**: Spring Security, JWT
* **API 문서화**: Springdoc OpenAPI (Swagger UI)
* **빌드 도구**: Gradle

---

## 📂 프로젝트 구조

```
com.example.auth
 ├─ AuthApplication.java
 ├─ config
 │   ├─ SecurityConfig.java
 │   └─ OpenApiConfig.java
 ├─ controller
 │   ├─ AdminController.java
 │   └─ AuthController.java
 ├─ domain
 │   ├─ Role.java
 │   ├─ User.java
 │   └─ UserRepository.java
 ├─ dto
 │   ├─ LoginRequest.java
 │   ├─ SignupRequest.java
 │   ├─ TokenResponse.java
 │   └─ UserResponse.java
 ├─ security
 │   ├─ JwtTokenProvider.java
 │   ├─ JwtAuthenticationFilter.java
 │   └─ CustomUserDetailsService.java
 ├─ service
 │   └─ UserService.java
 ├─ web
 │   ├─ AuthController.java
 │   └─ AdminController.java
 ├─ exception
 │   ├─ ApiException.java
 │   ├─ ErrorCode.java
 │   └─ GlobalExceptionHandler.java
 └─ test (src/test/...)

```

---

## ⚙️ 환경 변수 설정

`application.yml`은 민감 정보를 환경변수로 참조합니다. EC2나 로컬 환경에서 다음과 같이 설정합니다:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=authdb
export DB_USERNAME=appuser
export DB_PASSWORD=super-secret

export JWT_SECRET=my-very-long-256-bit-secret-key-which-is-256bit-long!
export JWT_VALIDITY=7200
```

---

## 🚀 실행 방법

1. **Gradle 빌드**

```bash
./gradlew clean build -x test
```

2. **EC2로 jar 파일 업로드**

```bash
scp -i "test-key.pem" build/libs/AuthSpring-0.0.1-SNAPSHOT.jar ubuntu@EC2_PUBLIC_IP:~
```

3. **EC2에서 실행**

```bash
java -jar AuthSpring-0.0.1-SNAPSHOT.jar
```

4. **Swagger UI 접속**

```
http://EC2_PUBLIC_IP:8080/swagger-ui.html
```

---

## 📌 API 예시

| HTTP Method | Endpoint                | 설명              |
|-------------|-------------------------|-----------------|
| POST        | /signup                 | 회원가입            |
| POST        | /login                  | 로그인             |
| POST        | /admin/users/{id}/roles | 특정 사용자 권한 업그레이드 |

> Swagger UI에서 전체 API 명세를 확인할 수 있습니다.
> http://3.39.11.49:8080/swagger-ui/index.html


---
