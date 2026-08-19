# JPA 엔티티 작성 현황

`docs/erd/kakao_chat_schema_v2.sql`의 21개 테이블 전부에 대해 JPA 엔티티 작성을 완료했습니다. 이 문서는 각 엔티티의 구조와 설계 의도를 한눈에 파악하기 위한 정리본입니다.

## 공통 설계 원칙

모든 엔티티에 예외 없이 적용된 규칙입니다.

| 규칙 | 이유 |
|---|---|
| `@Getter`만 사용, `@Setter`/`@Data` 없음 | 어디서든 값이 바뀌는 것을 막아 변경 이력 추적 가능하게 함 |
| `@NoArgsConstructor(access = AccessLevel.PROTECTED)` | JPA가 리플렉션/프록시 생성 시 필요하지만, 앱 코드에서 `new Entity()`로 빈 객체를 만드는 것은 차단 |
| `public static create(...)` 정적 팩토리 메서드 | 필수값을 강제하고 생성 의도를 이름으로 드러냄 (생성자 직접 노출 안 함) |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | DB의 `AUTO_INCREMENT`와 1:1 대응 (MySQL은 SEQUENCE 객체가 없음) |
| 모든 `@ManyToOne`/`@OneToOne`에 `fetch = FetchType.LAZY` 명시 | JPA 기본값이 EAGER라 그대로 두면 불필요한 연관 조회(N+1)가 발생하기 때문 |
| FK 컬럼에 `UNIQUE` 제약이 있으면 `@OneToOne`, 없으면 `@ManyToOne` | DB 제약조건을 그대로 JPA 매핑 선택 기준으로 사용 |
| `@OneToMany(mappedBy=...)` 역방향 매핑은 전부 미작성 | DB에는 영향 없는 순수 편의 기능이라, 실제 필요해질 때(Service/Repository 작성 시점)까지 보류 |

패키지는 `domain_spec.md`의 7개 도메인 기준으로 `com.example.springbootpractice.domain.<도메인>.entity` 아래에 배치했습니다.

---

## 1. 코어 엔티티 (FK 없음, 독립 테이블) — 4개

다른 테이블을 참조하지 않는, 연관관계 매핑이 필요 없는 가장 단순한 테이블들입니다. 가장 먼저 작성해서 엔티티 작성 패턴을 확립하는 데 썼습니다.

### User
- **파일**: `domain/user/entity/User.java`
- **테이블**: `USER`
- **PK**: `id` (IDENTITY)
- **필드**: `email`(unique,100), `password`(255), `phoneNumber`(unique,20), `oauthProvider`(nullable,20), `oauthId`(nullable,255), `createdAt`
- **팩토리**: `create(email, password, phoneNumber)` — oauth 필드는 일반 가입 시 비워둠, `createdAt`은 자동 채움
- **비고**: 모든 엔티티의 기준 패턴이 여기서 확립됨

### ChatRoom
- **파일**: `domain/chat/entity/ChatRoom.java`
- **테이블**: `CHAT_ROOM`
- **PK**: `id` (IDENTITY)
- **필드**: `roomName`(100), `roomType`(20 — DIRECT/GROUP/OPEN_DIRECT/OPEN_GROUP/MY), `createdAt`
- **팩토리**: `create(roomName, roomType)`

### Emoji
- **파일**: `domain/emoticon/entity/Emoji.java`
- **테이블**: `EMOJI`
- **PK**: `id` (IDENTITY)
- **필드**: `imageUrl`(255), `category`(100), `createdAt`
- **팩토리**: `create(imageUrl, category)`

### GiftProduct
- **파일**: `domain/gift/entity/GiftProduct.java`
- **테이블**: `GIFT_PRODUCT`
- **PK**: `id` (IDENTITY)
- **필드**: `productName`(50), `brandName`(50), `price`(`BigDecimal`, precision=10/scale=2), `imageUrl`(nullable,255), `createdAt`(**nullable** — DDL에 NOT NULL 없음)
- **팩토리**: `create(productName, brandName, price)`
- **비고**: 금액 컬럼이라 `double` 대신 `BigDecimal` 사용 (부동소수점 오차 방지)

---

## 2. FK를 가진 연관 엔티티 (단순 `@ManyToOne`/`@OneToOne`) — 14개

다른 테이블 하나 이상을 참조하지만, FK가 상대 테이블의 단일 PK를 가리키는 일반적인 경우입니다.

### Profile
- **파일**: `domain/user/entity/Profile.java` | **테이블**: `PROFILE`
- **PK**: `profileId` (IDENTITY, 컬럼명이 `id`가 아니라 `profile_id`)
- **연관관계**: `user` → `@ManyToOne(User)`, FK `user_id`
- **필드**: `nickname`(50), `statusMessage`(nullable,100), `profileImageUrl`(nullable,500), `profileType`(20), `createdAt`, `updatedAt`
- **팩토리**: `create(user, nickname, profileType)`
- **비고**: `(profile_id, user_id)` 복합 UNIQUE(`uq_user_profile`)를 가지고 있어서, 뒤에 나올 `RoomParticipant`가 이 조합을 복합 FK로 참조함

### AuthSession
- **파일**: `domain/user/entity/AuthSession.java` | **테이블**: `AUTH_SESSION`
- **연관관계**: `user` → `@OneToOne(User)` (user_id가 UNIQUE라서 유저당 세션 1개)
- **필드**: `deviceId`(255), `refreshToken`(500), `issuedAt`, `expiredAt`
- **팩토리**: `create(user, deviceId, refreshToken, expiredAt)`

### UserSubscription
- **파일**: `domain/user/entity/UserSubscription.java` | **테이블**: `USER_SUBSCRIPTION`
- **연관관계**: `user` → `@OneToOne(User)` (1유저당 1구독 레코드)
- **필드**: `isEmoticonPlusActive`(boolean, 기본값 false), `expiredAt`(nullable)
- **팩토리**: `create(user)` — 구독 비활성 상태로 시작

### DeviceToken
- **파일**: `domain/notification/entity/DeviceToken.java` | **테이블**: `DEVICE_TOKEN`
- **연관관계**: `user` → `@ManyToOne(User)` (한 유저가 여러 디바이스 보유 가능)
- **필드**: `deviceToken`(255), `deviceType`(20 — iOS/ANDROID), `updatedAt`
- **팩토리**: `create(user, deviceToken, deviceType)`

### NotificationSetting
- **파일**: `domain/notification/entity/NotificationSetting.java` | **테이블**: `NOTIFICATION_SETTING`
- **연관관계**: `user` → `@OneToOne(User)`
- **필드**: `isEnabled`(boolean, 기본 true), `isSound`(boolean, 기본 true), `quietStartTime`/`quietEndTime`(`LocalTime`, nullable)
- **팩토리**: `create(user)`

### Friend
- **파일**: `domain/friend/entity/Friend.java` | **테이블**: `FRIEND`
- **연관관계**: `user` → `@ManyToOne(User)`(FK `user_id`), `friend` → `@ManyToOne(User)`(FK `friend_id`), `targetProfile` → `@ManyToOne(Profile, nullable)`(FK `target_profile_id`)
- **필드**: `status`(20, 기본값 "ACTIVE"), `alias`(nullable,50), `createdAt`
- **팩토리**: `create(user, friend)`
- **비고**: **같은 테이블(USER)을 두 번 참조**하는 첫 사례 — 필드명과 `@JoinColumn`으로 역할을 구분(소유자/대상)

### UserEmoji
- **파일**: `domain/emoticon/entity/UserEmoji.java` | **테이블**: `USER_EMOJI`
- **연관관계**: `user` → `@ManyToOne(User)`, `emoji` → `@ManyToOne(Emoji)`
- **필드**: 없음 (매핑 전용 테이블)
- **팩토리**: `create(user, emoji)`

### GiftOrder
- **파일**: `domain/gift/entity/GiftOrder.java` | **테이블**: `GIFT_ORDER`
- **연관관계**: `sender`/`receiver` → `@ManyToOne(User)` 각각, `product` → `@ManyToOne(GiftProduct)`
- **필드**: `orderStatus`(20, 기본값 "SUCCESS"), `createdAt`
- **팩토리**: `create(sender, receiver, product)`

### Voucher
- **파일**: `domain/gift/entity/Voucher.java` | **테이블**: `VOUCHER`
- **연관관계**: `order` → `@OneToOne(GiftOrder)` (order_id UNIQUE — 주문당 교환권 1개)
- **필드**: `voucherCode`(100), `voucherStatus`(20, 기본값 "UNUSED"), `validUntil`(nullable), `usedAt`(nullable)
- **팩토리**: `create(order, voucherCode, validUntil)`

### CallLog
- **파일**: `domain/call/entity/CallLog.java` | **테이블**: `CALL_LOG`
- **연관관계**: `room` → `@ManyToOne(ChatRoom)`, `caller`/`receiver` → `@ManyToOne(User)` 각각
- **필드**: `callType`(20 — VOICE/FACE), `callStatus`(20 — CONNECTED/REJECTED/MISSED/BUSY), `durationSeconds`(int), `createdAt`
- **팩토리**: `create(room, caller, receiver, callType, callStatus, durationSeconds)`

### Message
- **파일**: `domain/chat/entity/Message.java` | **테이블**: `MESSAGE`
- **연관관계**: `room` → `@ManyToOne(ChatRoom)`, `sender` → `@ManyToOne(User)`
- **필드**: `messageType`(20), `isDeleted`(boolean, 기본 false), `createdAt`, `deletedAt`(nullable), `parentMessageId`(nullable, **일반 Long 필드**)
- **팩토리**: `create(room, sender, messageType)`
- **비고**: `parent_message_id`는 개념상 자기 자신(MESSAGE)을 가리키지만 DDL에 FK 제약이 없어서 연관관계로 만들지 않고 값 필드로만 매핑 (§3에서 상술)

### FileMetadata
- **파일**: `domain/chat/entity/FileMetadata.java` | **테이블**: `FILE_METADATA`
- **연관관계**: `message` → `@ManyToOne(Message)` (message_id UNIQUE 아님 → ManyToOne)
- **필드**: `fileUrl`(255), `originalName`(255), `fileSize`(long)
- **팩토리**: `create(message, fileUrl, originalName, fileSize)`

### EmojiMetadata
- **파일**: `domain/emoticon/entity/EmojiMetadata.java` | **테이블**: `EMOJI_METADATA`
- **연관관계**: `message` → `@ManyToOne(Message)`, `emoji` → `@ManyToOne(Emoji)`
- **필드**: 없음
- **팩토리**: `create(message, emoji)`

### GiftMetadata
- **파일**: `domain/gift/entity/GiftMetadata.java` | **테이블**: `GIFT_METADATA`
- **연관관계**: `message` → `@OneToOne(Message)`, `voucher` → `@OneToOne(Voucher)` (둘 다 UNIQUE)
- **필드**: 없음
- **팩토리**: `create(message, voucher)`

---

## 3. 특수 패턴 엔티티 — 3개

일반적인 단일 컬럼 FK 매핑을 벗어나는, 별도로 익혀야 하는 패턴들입니다.

### MessageTextContent — PK가 곧 FK (`@MapsId`)
- **파일**: `domain/chat/entity/MessageTextContent.java` | **테이블**: `MESSAGE_TEXT_CONTENT`
- **구조**: `message_id` 컬럼이 PK이면서 동시에 `MESSAGE`를 가리키는 FK
- **매핑**:
  ```java
  @Id
  @Column(name = "message_id")
  private Long messageId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "message_id")
  private Message message;
  ```
- **핵심 포인트**: `@GeneratedValue`가 없음 — 내 PK를 직접 채번하지 않고 `message`의 PK를 그대로 물려받음(`@MapsId`)
- **필드**: `content`(`@Lob`, TEXT)
- **팩토리**: `create(message, content)`

### RoomParticipant — 복합 FK (`@JoinColumns`)
- **파일**: `domain/chat/entity/RoomParticipant.java` | **테이블**: `ROOM_PARTICIPANT`
- **구조**: `(user_id, profile_id)` 두 컬럼이 합쳐져서 `PROFILE`의 복합 UNIQUE 키(`profile_id`, `user_id`)를 참조
- **매핑**:
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false),
      @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
  })
  private Profile profile;
  ```
- **연관관계**: `room` → `@ManyToOne(ChatRoom)` (단순 FK), `profile` → 위의 복합 FK
- **필드**: `joinedAt`, `lastReadMessageId`(**일반 Long**, FK 제약 없음), `customRoomName`(nullable,100), `isMuted`/`isPinned`(boolean, 기본 false), `backgroundImageUrl`(nullable,500)
- **팩토리**: `create(room, profile, lastReadMessageId)`
- **비고**: `ROOM_PARTICIPANT.user_id`를 가리키는 `USER` 직접 FK는 DB에 없음 — `profile.getUser()`로 타고 들어가면 됨. 실제 Hibernate가 이 매핑을 SQL로 정확히 풀어내는지는 DB 연결 후 검증 필요 (아직 미검증)

### MessageLog — FK 제약이 없는 로그 테이블
- **파일**: `domain/chat/entity/MessageLog.java` | **테이블**: `MESSAGE_LOG`
- **구조**: `message_id`, `room_id`, `sender_id`가 각각 `MESSAGE`/`CHAT_ROOM`/`USER`를 개념적으로 가리키지만, DDL의 `ALTER TABLE ... ADD FOREIGN KEY` 목록에 이 테이블에 대한 항목이 전혀 없음 (원본이 삭제되어도 로그는 남아야 하는 감사 로그의 특성상 의도적으로 제약을 걸지 않은 것으로 추정)
- **매핑**: 세 컬럼 모두 연관관계가 아닌 **순수 `Long` 필드**로 매핑
- **필드**: `messageId`, `roomId`, `senderId`, `actionType`(255), `rawContent`(`@Lob`, TEXT), `createdAt`
- **팩토리**: `create(messageId, roomId, senderId, actionType, rawContent)`

---

## 4. 도메인별 패키지 매핑

| 도메인 패키지 | 소속 엔티티 |
|---|---|
| `domain.user.entity` | User, Profile, AuthSession, UserSubscription |
| `domain.notification.entity` | DeviceToken, NotificationSetting |
| `domain.friend.entity` | Friend |
| `domain.chat.entity` | ChatRoom, Message, MessageTextContent, FileMetadata, MessageLog, RoomParticipant |
| `domain.emoticon.entity` | Emoji, EmojiMetadata, UserEmoji |
| `domain.gift.entity` | GiftProduct, GiftOrder, Voucher, GiftMetadata |
| `domain.call.entity` | CallLog |

**총 21개 엔티티, 21개 테이블 매핑 완료.**

---

## 5. 아직 진행하지 않은 것

- **`@OneToMany(mappedBy=...)` 역방향 매핑** — 예: `User.profiles`, `ChatRoom.messages` 등. DB에는 영향 없는 순수 편의 기능이라 실제 조회 요구사항이 생길 때 추가 예정 ([[jpa-concepts]] 참고)
- **DB 연결 설정** — `application.properties`에 datasource가 없어서 실제 MySQL과의 정합성(특히 `RoomParticipant`의 복합 FK 매핑)이 아직 검증되지 않음
- **Repository 인터페이스** — `JpaRepository`를 상속하는 인터페이스들이 아직 없음
