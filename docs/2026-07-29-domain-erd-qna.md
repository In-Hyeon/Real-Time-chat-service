# 도메인 스펙 & ERD 검토 Q&A (2026-07-29)

카카오톡 클론코딩 스터디에서 `docs/domain_spec.md`와 `docs/erd/` 스키마를 검토하며 나눈 질문/답변과, 그 결과로 반영된 변경사항을 정리한 기록이다.

---

## 1. `도메인 수정 사항.md`가 `domain_spec.md`에 잘 반영됐는지 검토

**Q. `도메인 수정 사항.md`에 적어놓은 질문/체크리스트들이 `domain_spec.md`에 잘 반영됐는지 검토해줘.**

A. 대부분 반영되어 있었다.

- ✅ 국가 코드 포함 전화번호, 1인 1계정, 멀티프로필 최대 3개 제한, 친구 복합 인덱스, 나와의 채팅방, 채팅방 개인화 설정, 이모티콘 플러스 구독 처리, 보이스톡/페이스톡·커머스 도메인 신규 작성 → 모두 반영됨.
- ⚠️ 다만 "실제 카카오톡은 어떤 인증 방식을 쓰는지", "실제 카카오톡이 진짜 WebSocket을 쓰는지", "Message Queue가 뭔지" 같은 **리서치성 질문**들은 spec에 "우리 시스템의 설계 결정"으로만 대체되어 있고, 실제 리서치 답변은 빠져 있었다.
- ⚠️ "채팅방 메타데이터는 어떻게 구성돼야 하는가"는 `CHAT_ROOM` 자체 필드 설계가 거의 없어 가장 미반영에 가까운 항목이었다.

---

## 2. `domain_spec.md`가 실제 ERD(`docs/erd/`)에 반영 안 된 문제

**Q. `domain_spec.md`의 내용이 실제 ERD에는 반영이 안 되어 있다는 게 문제인데, 확인하고 어떻게 수정해야 하는지 설명해줘.**

A. `kakao_chat_schema.sql` / `kakao_chat_erd.json`을 직접 열어 필드 단위로 대조한 결과, spec에 적힌 정책 중 상당수가 실제 테이블 컬럼으로 존재하지 않았다.

| 도메인 | 문제 |
|---|---|
| 유저/인증 | `USER`에 `phone_number` 자체가 없음 (이메일 로그인 구조뿐). 세션 관리용 컬럼도 없음 |
| 친구 | `FRIEND`에 `status`, `alias`, `target_profile_id` 없음. `(user_id, friend_id)` 유니크 제약도 실제로는 없음(문서에만 있다고 적혀 있었음) |
| 채팅 | `CHAT_ROOM.room_type`에 `'MY'` 값 없음. `ROOM_PARTICIPANT`에 개인화 필드(`custom_room_name`, `is_muted`, `is_pinned`, `background_image_url`) 없음 |
| 이모티콘 | 구독 상태를 관리할 `USER_SUBSCRIPTION` 테이블 자체가 없음 |
| 알림 | `quiet_start_time`/`quiet_end_time`이 스펙엔 `TIME`이라 적혀있지만 실제론 `TIMESTAMP` |
| 커머스 | `message_type`에 `'GIFT'`가 없고, `MESSAGE`와 `VOUCHER`를 연결할 컬럼도 없음 |

**수정 방향:** 원본 ERD 툴(drawDB 추정)에서 JSON을 다시 불러와 위 컬럼들을 추가하고, JSON·SQL·PNG를 세트로 다시 export하기로 함.

---

## 3. 컬럼명/데이터 타입 정리 요청

**Q. 데이터 타입이랑 컬럼명까지 어떻게 수정하면 좋을지 정리해서 보여줘.**

A. 도메인별로 컬럼명·타입·제약조건·설명을 표로 정리해서 제시. 예: `USER.phone_number VARCHAR(20) NOT NULL UNIQUE`, `FRIEND.status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'`, `ROOM_PARTICIPANT.is_muted BOOLEAN NOT NULL DEFAULT false`, 신규 테이블 `USER_SUBSCRIPTION` 등. (최종 확정본은 6절 참고)

---

## 4. USER 테이블이 무거워지는 문제 — AUTH_SESSION 분리

**Q. 유저 테이블에 컬럼이 추가되면서 무거워지는 것 같은데, 디바이스 테이블을 따로 분리해서 핸드폰 번호랑 디바이스 id 같은 컬럼을 모아놔도 되나?**

A. `phone_number`와 `device_id`를 같은 테이블로 묶는 건 방향이 다르다.

- **`phone_number`는 분리하면 안 됨:** 이메일처럼 "자주 안 바뀌는 신원 정보"이고 "1전화번호=1계정" 유니크 제약이 걸려야 해서 `USER`와 1:1로 묶여 있어야 한다. 별도 테이블(1:N)로 빼면 오히려 `USER`/`PROFILE` 3NF 분리 원칙(자주 안 바뀌는 신원 정보는 한 곳에)에 위배된다.
- **`active_device_id`(세션 정보)는 분리하는 게 맞음:** 로그인마다 바뀌는 휘발성 데이터라서, 오히려 `USER`를 무겁게 만드는 원인이다. 이름은 `DEVICE`보다 **`AUTH_SESSION`**이 정확함.
- `AUTH_SESSION.user_id`에 `UNIQUE`를 걸면 "1인 1계정 단일 활성 세션" 정책을 DB 레벨에서 자연스럽게 강제할 수 있음(신규 로그인 시 UPSERT로 기존 세션 자동 교체).
- 기존 `DEVICE_TOKEN`(푸시 알림용, 유저당 여러 기기 1:N)과는 성격이 달라 합치면 안 됨.

**결과:** `AUTH_SESSION` 테이블 신설 (`id`, `user_id UNIQUE`, `device_id`, `refresh_token`, `issued_at`, `expired_at`).

---

## 5. AUTH_SESSION은 어느 도메인 소속인가

**Q. AUTH_SESSION은 그러면 어디 도메인에 속하는거야?**

A. **유저 및 인증 도메인**에 속한다. `domain_spec.md`의 도메인 정의 자체에 이미 "세션/토큰 관리"가 명시돼 있었기 때문에, 새 도메인을 만들 필요 없이 기존 도메인의 "주요 엔티티" 목록에만 추가하면 됨.

---

## 6. PK/FK 표기 관련 기초 질문

**Q. `FK → USER.id`의 의미는 외래키가 USER 테이블의 id라는거야?**

A. 맞다. "이 컬럼이 외래키이고, 참조 대상은 `USER.id`(PK)"라는 뜻. FK 컬럼의 타입은 참조 대상 PK 타입과 반드시 동일해야 한다(`BIGINT UNSIGNED` ↔ `BIGINT UNSIGNED`).

**Q. 기본 키: USER, 외래 키: AUTH_SESSION 이렇게 맞아?**

A. 테이블이 아니라 **컬럼** 기준으로 말해야 정확하다.
- PK = `USER` 테이블의 `id` 컬럼
- FK = `AUTH_SESSION` 테이블의 `user_id` 컬럼 (USER.id를 참조)
- 한 테이블이 PK와 FK를 동시에 가질 수도 있다.

---

## 7. FRIEND의 복합 UNIQUE 제약이 다대일 관계를 깨는지

**Q. `ALTER TABLE FRIEND ADD CONSTRAINT uq_friend_user_friend UNIQUE (user_id, friend_id);` 이건 왜 필요한거야? 이러면 다대일 관계가 안되지 않나?**

A. 안 깨진다. 다대일 관계는 FK 자체가 만드는 것이고, `UNIQUE(user_id, friend_id)`는 **완전히 동일한 조합이 두 번 저장되는 것만** 막는다.
- `(1,2)`, `(1,3)`, `(1,4)` → 전부 허용 (user_id 반복 OK)
- `(5,2)`, `(6,2)` → 전부 허용 (friend_id 반복 OK)
- `(1,2)`를 두 번 저장 → 이것만 차단 (동일 친구 중복 등록 방지)

---

## 8. ROOM_PARTICIPANT 테이블이 커지는 문제 — 개인설정 테이블 분리 여부

**Q. ROOM_PARTICIPANT 테이블이 너무 커지는데 상관없을까? is_muted나 is_pinned 같은 항목들은 따로 개인설정 테이블을 만들어서 참조하는게 좋을거 같은데**

A. 분리하지 않는 게 낫다는 결론.

- 컬럼 10개는 실무 기준으로 전혀 큰 게 아님.
- `is_muted`/`is_pinned`/`custom_room_name`/`background_image_url`은 `(room_id, user_id)`와 1:1이고, 변경 빈도가 낮으며, 채팅방 목록 조회 시 항상 함께 조회됨 → 분리해도 이득 없이 JOIN만 늘어남.
- 오히려 분리 후보는 **`last_read_message_id`**다. 메시지를 읽을 때마다 계속 UPDATE되는 hot 컬럼이라, `MESSAGE`/`MESSAGE_TEXT_CONTENT` 수직 분할과 같은 논리로 분리를 고려할 수 있음. 다만 지금 단계에서는 과도한 최적화이므로 나중에 트래픽 문제가 실제로 생기면 그때 고려하기로 함.

---

## 9. GIFT_METADATA를 왜 또 만드는지 (GIFT_PRODUCT와의 차이)

**Q. GIFT_METADATA는 GIFT_PRODUCT가 있는데 굳이 새로 생성하는 이유는 뭐야?**

A. 역할이 완전히 다르다.

- `GIFT_PRODUCT`: 상품 카탈로그(마스터 데이터) — 채팅/주문과 무관하게 존재.
- `GIFT_ORDER`: 결제 트랜잭션.
- `VOUCHER`: 결제 성공 시 발급되는 실제 교환권.
- `GIFT_METADATA`: **어떤 채팅 메시지가 어떤 VOUCHER를 보여주는지 연결하는 다리 역할.**

`GIFT_PRODUCT`는 `MESSAGE`나 `VOUCHER`를 참조하지 않기 때문에 이 연결을 대신할 수 없다. `voucher_id`를 `MESSAGE`에 직접 컬럼으로 넣지 않고 별도 테이블로 뺀 이유는, GIFT가 아닌 메시지에서는 그 컬럼이 대부분 NULL이 되기 때문(`EMOJI_METADATA`, `FILE_METADATA`와 동일한 위성 테이블 패턴).

**Q. `message_id`, `voucher_id` 둘 다 일대일 관계야, 다대일 관계야?**

A. FK만 있으면 기본은 **다대일**이다(여러 `GIFT_METADATA` 행이 같은 메시지/교환권을 가리키는 걸 DB가 막지 않음). 하지만 의미상 "메시지 하나 = 교환권 하나"가 맞으므로, `UNIQUE` 제약을 각각 걸어서 진짜 1:1로 강제해야 한다. (`EMOJI_METADATA.emoji_id → EMOJI`는 반대로 진짜 다대일이 맞음 — `EMOJI`는 여러 메시지에서 재사용되는 카탈로그이기 때문.)

**반영 결과:** `GIFT_METADATA.message_id`, `voucher_id` 둘 다 `UNIQUE` 추가.

---

## 10. USER_SUBSCRIPTION의 소속 도메인

**Q. USER_SUBSCRIPTION는 이모티콘 도메인에 속하나?**

A. 그렇다. `domain_spec.md`의 이모티콘 도메인 정의에 "구독 상품을 지원하는 도메인"이라고 이미 명시돼 있었음. 다만 향후 이모티콘 외 다른 구독 상품이 생기면 `subscription_type` 컬럼을 추가해 범용화하거나 별도 도메인으로 승격하는 것을 고려할 수 있음.

---

## 11. 최종 ERD(v2) 검토 — 발견 및 수정된 버그

**Q. `kakao_chat_erd_v2.sql`로 수정한 내용이 맞는지 검토해줘.** (1차)

A. 대부분 정확했지만 두 가지 문제 발견:
1. 🔴 `GIFT_METADATA.message_id`, `voucher_id`에 `UNSIGNED`가 빠짐 (참조 대상 `MESSAGE.id`/`VOUCHER.id`는 `BIGINT UNSIGNED`인데 타입 불일치).
2. ⚠️ `USER.oauth_provider`, `FRIEND.status`가 `VARCHAR(255)`로 되어 있어, 다른 열거형 컬럼들(`room_type`, `message_type` 등 `VARCHAR(20)`)과 일관성이 깨짐.

**Q. (v2 파일들) 이제 마지막으로 검토해줘.** (2차, 최종)

A. 두 문제 모두 수정 확인:
- `GIFT_METADATA.message_id`/`voucher_id` → `BIGINT UNSIGNED NOT NULL UNIQUE`로 수정됨.
- `USER.oauth_provider`, `FRIEND.status` → `VARCHAR(20)`으로 수정됨.

PNG에서도 `GIFT_METADATA`가 `MESSAGE`/`VOUCHER`와 `1...1` 카디널리티로 정확히 그려진 것 확인. (참고: `room_id` 등 일부 FK 컬럼에 `UNSIGNED`가 빠진 건 v1부터 있던 pre-existing 이슈라 이번 스코프에서는 보류.)

---

## 12. domain_spec.md 최종 반영

**Q. 응 domain_spec.md도 반영해서 업데이트해줘.**

A. ERD v2 기준으로 `domain_spec.md` 업데이트 완료:

- **주요 엔티티 목록 갱신:** 유저 도메인 `+AUTH_SESSION`, 채팅 도메인 `+MESSAGE_LOG`, 이모티콘 도메인 `+USER_SUBSCRIPTION`, 커머스 도메인 `+GIFT_METADATA`
- **유저 도메인:** `AUTH_SESSION.user_id UNIQUE`로 단일 세션 정책을 DB 레벨에서 강제한다는 설명, Refresh Token 저장 위치(`AUTH_SESSION.refresh_token`), OAuth 필드(`oauth_provider`, `oauth_id`) 명시. DB 설계 포인트에 AUTH_SESSION 분리 근거 추가.
- **채팅 도메인:** `message_type='GIFT'` 추가 사실, `MESSAGE_LOG` 감사 로그 기능 설명 추가.
- **이모티콘 도메인:** "USER 또는 USER_SUBSCRIPTION"이라는 애매한 표현을 `USER_SUBSCRIPTION`으로 확정.
- **커머스 도메인:** "MESSAGE에 페이로드로 voucher_id 연결"이라는 표현을 실제 구현(`GIFT_METADATA` 위성 테이블, 양쪽 UNIQUE로 1:1 강제)에 맞게 재작성.

---

## 최종 산출물

- `docs/domain_spec.md` — 최종 업데이트됨
- `docs/erd/kakao_chat_schema_v2.sql`, `kakao_chat_erd_v2.json`, `kakao_chat_erd_v2.png` — 최종 ERD
- 세 파일 모두 서로 일치하는 상태로 확인 완료
