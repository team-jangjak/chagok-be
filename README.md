# 차곡 백엔드

습관 형성을 돕는 차곡 서비스의 백엔드 애플리케이션입니다. 사용자 인증과 소셜 로그인, 습관 관리, 통계·알림과 같은 확장을 고려한 인프라 구성을 제공하여 안정적으로 습관 데이터를 저장하고 서비스와 연동합니다.

## 주요 기능

- **회원 관리**: 자체 회원 가입과 JWT 기반 인증, 액세스·리프레시 토큰 재발급, 로그아웃 처리
- **소셜 로그인**: 구글·카카오 OAuth 연동, OpenFeign 기반 외부 API 호출
- **습관 도메인**: 사용자 맞춤 습관 생성, 액션 단위 진행 현황 관리, 카테고리 검증
- **인프라 구성**: PostgreSQL, Redis(Cache & 토큰 관리), Kafka(이벤트 확장용) 기반 확장성 고려
- **운영 편의성**: Batch 구성으로 모니터링과 배치 작업 지원, Swagger UI 제공

## 기술 스택

- **언어 및 프레임워크**: Java 17, Spring Boot 3.5
- **주요 의존성**: Spring Web, Spring Security, Spring Data JPA, Spring Batch, Spring Cloud OpenFeign, Spring Kafka, Spring Data Redis, Lombok, Springdoc OpenAPI
- **데이터베이스**: PostgreSQL (Docker Compose로 로컬 실행)
- **빌드 도구**: Gradle (wrapper 포함)
- **기타**: Redis, Docker Compose, JWT (io.jsonwebtoken)

## 사전 준비

- JDK 17 이상
- Docker & Docker Compose
- 로컬 실행 시 Redis 서버 (필요 시 Docker로 별도 실행 가능)
- 환경 변수 또는 `application-*.yml` 파일에 아래 값을 설정
  - `spring.datasource.*` (DB 접속 정보)
  - `jwt.secret-key`, `jwt.refresh-secret-key`, `jwt.expiration`, `jwt.refresh-expiration`
  - `google.*`, `oauth2.kakao.*` (OAuth 클라이언트 정보)
  - `app.frontend-base-url` (프런트엔드 베이스 URL)

> 예시 값들은 레포지토리의 `application-dev.yml`을 참고하여 별도의 비공개 파일이나 환경 변수로 관리해 주세요.

## 로컬 실행 가이드

1. **레포지토리 클론**
   ```bash
   git clone <repository-url>
   cd chagok
   ```

2. **데이터베이스 기동**
   ```bash
   docker-compose up -d
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

   또는 패키징 후 실행:
   ```bash
   ./gradlew clean build
   java -jar build/libs/chagok-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
   ```

4. **API 문서 확인**
   - 애플리케이션 실행 후 `http://localhost:8080/swagger-ui/index.html`

5. **서버 요청 주소**
   - 'http://localhost:8080'

## 테스트

단위 및 통합 테스트는 Gradle을 통해 실행할 수 있습니다.

```bash
./gradlew test
```

## 개발 편의 사항

- `src/main/resources/application.yml`은 공통 설정만 포함하고, 실제 환경별 값은 프로필(`application-dev.yml`, `application-prod.yml`)로 관리하세요.
- Redis·Kafka 등의 외부 리소스는 로컬 개발 환경에 따라 별도 컨테이너 또는 호스트 서비스를 사용하십시오.
- 새로운 의존성을 추가할 때는 `build.gradle`의 버전 전략(springCloudVersion 등)을 참고하여 일관되게 관리합니다.
