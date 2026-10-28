# springboot-study-2026
### Study Topic: Real-time Chat Service
2026년 GDGoC 스프링 부트 개념 및 실습 스터디 저장소

카카오톡을 벤치마킹한 실시간 채팅 서비스를 직접 설계하고 구현하며 Spring Boot와 JPA를 학습하는 스터디 프로젝트입니다.

### kakao_chat_erd_v2
<img width="5295" height="3744" alt="kakao_chat_erd_v2" src="https://github.com/user-attachments/assets/71f073cf-f00a-4298-b6c5-dcd938420443" />

## 기술 스택

- Java 17
- Spring Boot 4.1.0 (Spring Data JPA, Spring Web MVC, Spring Security, WebSocket)
- WebSocket(STOMP) 실시간 메시지
- JWT (jjwt 0.12.6) — Access Token 30분 / Refresh Token 14일
- Gradle
- MySQL 8 (mysql-connector-j)
- Lombok

## 도메인 구성

`docs/domain_spec.md`에 정의된 7개 도메인 기준으로 패키지를 구성했습니다.

| 도메인 | 설명 |
|---|---|
| `user` | 회원, 프로필, 인증 세션, 이모티콘 플러스 구독 |
| `friend` | 친구 관계 |
| `chat` | 채팅방, 참가자, 메시지, 첨부파일, 메시지 로그 |
| `emoticon` | 이모티콘, 보유/사용 내역 |
| `notification` | 디바이스 토큰, 알림 설정 |
| `call` | 통화 기록 |
| `gift` | 선물 상품, 주문, 교환권 |

## 진행 현황

- [x] ERD 설계 및 DDL 작성 (`docs/erd/kakao_chat_schema_v2.sql`)
- [x] 도메인 명세 정리 (`docs/domain_spec.md`)
- [x] 21개 테이블 전체 JPA 엔티티 작성 (`src/main/java/.../domain/*/entity`)
- [x] DB 연결 설정 (`application.properties` + `application-local.properties`)
- [x] Repository / Service / Controller 레이어 (7개 도메인 전체)
- [x] API 응답 통일(`ApiResponse`) 및 전역 예외 처리(`BusinessException` + `ErrorCode`)
- [x] JWT 인증/인가 (로그인, 토큰 재발급, 로그아웃)
- [x] 실시간 채팅 (WebSocket + STOMP, CONNECT 인증 / SUBSCRIBE·SEND 참여자 확인)
- [x] 테스트 55개 통과

## 참고 문서

- [`docs/domain_spec.md`](docs/domain_spec.md) — 도메인 명세
- [`docs/erd/kakao_chat_schema_v2.sql`](docs/erd/kakao_chat_schema_v2.sql) — 최종 DDL (엔티티 매핑 기준)
- [`docs/entity-summary.md`](docs/entity-summary.md) — 21개 엔티티 상세 정리
- [`docs/logs/`](docs/logs) — 단계별 진행 기록과 트러블슈팅
- [`docs/study/jpa-concepts.md`](docs/study/jpa-concepts.md) — JPA 개념 정리 (Entity, 연관관계의 주인, Fetch 전략 등)
- [`imports.md`](imports.md) — 엔티티에서 사용한 import 라이브러리 정리

## 실행 방법

MySQL 8을 실행하고, `src/main/resources/application-local.properties`를 만들어 아래 값을 채웁니다 (git에 올라가지 않는 파일입니다).

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/<DB 이름>
spring.datasource.username=<사용자>
spring.datasource.password=<비밀번호>
jwt.secret=<32바이트 이상 임의 문자열>
```

```bash
./gradlew bootRun   # http://localhost:8080
./gradlew test      # 테스트 실행
```

테이블은 앱이 시작할 때 `schema.sql`이 만듭니다. 실시간 채팅은 브라우저 탭 두 개로 `http://localhost:8080/chat-test.html`을 열어 확인할 수 있습니다.
