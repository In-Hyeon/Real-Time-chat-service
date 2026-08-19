# springboot-study-2026
### Study Topic: Real-time Chat Service
2026년 GDGoC 스프링 부트 개념 및 실습 스터디 저장소

카카오톡을 벤치마킹한 실시간 채팅 서비스를 직접 설계하고 구현하며 Spring Boot와 JPA를 학습하는 스터디 프로젝트입니다.

### kakao_chat_erd_v2
<img width="5295" height="3744" alt="kakao_chat_erd_v2" src="https://github.com/user-attachments/assets/71f073cf-f00a-4298-b6c5-dcd938420443" />

## 기술 스택

- Java 17
- Spring Boot 4.1.0 (Spring Data JPA, Spring Web MVC)
- Gradle
- MySQL (mysql-connector-j)
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
- [ ] DB 연결 설정 (`application.properties`)
- [ ] Repository / Service / Controller 레이어
- [ ] 실시간 채팅 기능 구현 (WebSocket 등)

## 참고 문서

- [`docs/domain_spec.md`](docs/domain_spec.md) — 도메인 명세
- [`docs/erd/kakao_chat_schema_v2.sql`](docs/erd/kakao_chat_schema_v2.sql) — 최종 DDL (엔티티 매핑 기준)
- [`docs/jpa-concepts.md`](docs/jpa-concepts.md) — JPA 개념 정리 (Entity, 연관관계의 주인, Fetch 전략 등)
- [`docs/entity-summary.md`](docs/entity-summary.md) — 21개 엔티티 상세 정리
- [`imports.md`](imports.md) — 엔티티에서 사용한 import 라이브러리 정리

## 실행 방법

현재는 DB 연결 설정 전 단계라 엔티티 컴파일까지만 확인 가능합니다.

```bash
./gradlew compileJava
```
