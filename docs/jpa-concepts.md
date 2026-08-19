# JPA 엔티티/연관관계 매핑 개념 정리

스프링부트/JPA를 처음 접하는 상태에서, `docs/erd/kakao_chat_schema_v2.sql`을 실제 엔티티 클래스로 옮기기 전에 알아야 할 핵심 개념을 정리한 문서다. 우리 ERD(`USER`, `PROFILE`, `AUTH_SESSION` 등)를 예시로 설명한다.

---

## 1. JPA가 뭔가요? — 객체와 테이블을 연결하는 다리

지금까지는 `CREATE TABLE USER (...)`처럼 SQL로 테이블을 설계했다. 하지만 자바 코드에서 데이터를 다루려면 결국 자바 객체(클래스)가 필요하다.

문제는 SQL 세계(테이블/행/컬럼)와 자바 세계(클래스/객체/필드)가 서로 다른 방식으로 생겼다는 것. 이 둘을 자동으로 연결해주는 기술이 **ORM(Object-Relational Mapping)**이고, **JPA(Java Persistence API)**는 자바 진영의 ORM 표준 규격이다. (스프링부트는 내부적으로 `Hibernate`라는 구현체를 쓴다.)

```
SQL 테이블 USER          ⟷          자바 클래스 User
┌──────────────┐                    ┌──────────────────────────────┐
│ id           │                    │ private Long id;             │
│ email        │        JPA         │ private String email;        │
│ phone_number │  ◄──────────────►  │ private String phoneNumber;  │
└──────────────┘                    └──────────────────────────────┘
```

JPA 없이 직접 SQL을 짜던 방식(JDBC)과 비교하면, JPA를 쓰면 `INSERT INTO USER ...` 같은 SQL을 직접 안 쓰고 `userRepository.save(user)`처럼 자바 객체를 다루는 것만으로 DB 작업이 처리된다.

---

## 2. `@Entity` — 클래스를 테이블에 연결하기

```java
@Entity
@Table(name = "USER")
public class User {
    // ...
}
```

- `@Entity`: 이 클래스가 JPA가 관리하는 엔티티임을 선언
- `@Table(name = "USER")`: 매핑될 테이블 이름 지정 (클래스명과 테이블명이 같으면 생략 가능하지만, 명시하는 걸 권장)

---

## 3. 기본키(PK) 매핑 — `@Id`, `@GeneratedValue`

우리 스키마의 모든 PK는 `BIGINT UNSIGNED AUTO_INCREMENT`였다. 이걸 자바로 옮기면:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

- `@Id`: 이 필드가 기본키(PK)라는 뜻 (각 행을 유일하게 구분하는 컬럼)
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: DB의 `AUTO_INCREMENT`에게 값 생성을 위임한다는 뜻. MySQL `AUTO_INCREMENT`를 쓰는 우리 스키마에는 `IDENTITY`가 맞음

---

## 4. 일반 컬럼 매핑 — `@Column`

```java
@Column(name = "phone_number", nullable = false, unique = true, length = 20)
private String phoneNumber;
```

SQL에서 썼던 제약조건이 그대로 대응된다:

| SQL | JPA |
|---|---|
| `NOT NULL` | `nullable = false` |
| `UNIQUE` | `unique = true` |
| `VARCHAR(20)` | `length = 20` |
| 컬럼명 `phone_number` | `name = "phone_number"` |

---

## 5. 연관관계 매핑 — 가장 핵심이자 헷갈리는 부분

ERD에서 FK로 연결했던 관계들(`PROFILE.user_id → USER.id` 등)을 자바 객체 관계로 표현하는 부분.

### 5-1. 방향성: 단방향 vs 양방향

SQL의 FK는 방향이 없다(`PROFILE`이 `USER`를 참조한다는 사실 하나뿐). 하지만 자바 객체는 "누가 누구를 필드로 들고 있는가"로 방향이 생긴다.

- **단방향**: `Profile` 클래스만 `User user` 필드를 가짐 (User → Profile 접근 불가)
- **양방향**: `Profile`은 `User user`를, `User`는 `List<Profile> profiles`를 서로 가짐 (양쪽에서 서로 탐색 가능)

### 5-2. "연관관계의 주인" — 초보자가 제일 헷갈리는 개념

**중요한 규칙: DB에 FK 컬럼을 실제로 갖고 있는 쪽(=주인)만 그 관계를 저장/수정할 수 있다.**

`PROFILE` 테이블에 `user_id`라는 FK 컬럼이 실제로 존재하므로, **`Profile` 엔티티가 이 관계의 "주인(owner)"**이다. `User` 쪽에서 `profiles` 리스트를 아무리 조작해도 DB에는 반영되지 않고, 오직 `Profile.setUser(user)`를 해야 실제 FK 값이 바뀐다.

```java
@Entity
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   // 실제 FK 컬럼 — Profile이 "주인"
    private User user;
}

@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user")   // "나는 주인이 아니고, Profile.user 필드가 주인이다"
    private List<Profile> profiles = new ArrayList<>();
}
```

- `@JoinColumn(name = "user_id")`: 실제 FK 컬럼명을 지정. **주인 쪽에만 붙인다.**
- `mappedBy = "user"`: "나(User)는 주인이 아니라, 상대방(Profile)의 `user`라는 필드 이름으로 이미 매핑돼 있다"는 뜻. **주인이 아닌 쪽에 붙는 표시**일 뿐, 실제 DB에는 아무 영향 없음.

판단 기준은 간단하다: **SQL에서 FK 컬럼이 어느 테이블에 있는지 보면 그게 주인**이다. 우리 스키마에서는:
- `PROFILE.user_id` 존재 → `Profile`이 주인
- `AUTH_SESSION.user_id` 존재 → `AUTH_SESSION`이 주인
- `ROOM_PARTICIPANT.room_id`, `user_id` 존재 → `ROOM_PARTICIPANT`이 주인

### 5-3. 관계 종류별 어노테이션

| 관계 | 어노테이션 | 우리 스키마 예시 |
|---|---|---|
| 다대일 (N:1) | `@ManyToOne` | `Profile.user`, `Friend.user`, `Message.sender` |
| 일대다 (1:N) | `@OneToMany` | `User.profiles` (N:1의 반대쪽) |
| 일대일 (1:1) | `@OneToOne` | `AuthSession.user`, `UserSubscription.user`, `GiftMetadata.message` |
| 다대다 (N:M) | `@ManyToMany` | 우리 스키마엔 없음 (전부 중간 테이블로 N:1+N:1로 풀어놨음, 예: `USER_EMOJI`) |

우리 스키마는 `USER_EMOJI`, `EMOJI_METADATA`처럼 N:M을 직접 안 쓰고 중간 테이블(N:1 두 개)로 풀어놓은 구조라서, `@ManyToMany`를 쓸 일은 거의 없다. (실무에서도 `@ManyToMany`는 세밀한 제어가 안 돼서 잘 안 씀.)

### 5-4. Fetch 전략 — LAZY vs EAGER

연관된 엔티티를 **언제 DB에서 가져올지** 정하는 옵션.

- `FetchType.LAZY`: 실제로 그 필드에 접근하는 순간에만 추가 쿼리로 가져옴 (지연 로딩)
- `FetchType.EAGER`: 엔티티를 조회하는 즉시 연관된 것도 같이 가져옴 (즉시 로딩)

**결론: 항상 `LAZY`를 명시적으로 쓰는 걸 권장.** `@ManyToOne`/`@OneToOne`은 기본값이 `EAGER`라서 신경 안 쓰면 필요 없는 데이터까지 계속 같이 불러와서 성능 문제가 생기기 쉽다. `@OneToMany`/`@ManyToMany`는 기본값이 `LAZY`라 괜찮지만, 습관적으로 항상 명시해주는 게 안전하다.

### 5-5. Cascade — 부모를 저장/삭제할 때 자식도 같이 처리할지

```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Profile> profiles = new ArrayList<>();
```

- `cascade = CascadeType.ALL`: `User`를 저장/삭제하면 연관된 `Profile`들도 자동으로 같이 저장/삭제됨
- `orphanRemoval = true`: `profiles` 리스트에서 특정 `Profile`을 제거하면, 그 `Profile`이 DB에서도 삭제됨

관계마다 신중하게 정해야 한다. 예를 들어 `User` 삭제 시 `Profile`은 같이 지워지는 게 자연스럽지만(`CascadeType.ALL` 적합), `User` 삭제 시 `GIFT_ORDER`(결제 이력)까지 같이 지워지면 안 된다. 이건 엔티티를 하나씩 작성할 때 같이 판단한다.

---

## 6. 우리 스키마로 전체 그림 연결해보기

엔티티 클래스를 작성할 때 머릿속에서 이런 순서로 판단하면 된다:

1. **이 테이블에 FK 컬럼이 있는가?** → 있으면 그 컬럼은 `@ManyToOne`(또는 `@OneToOne`) + `@JoinColumn`으로 매핑 (주인)
2. **이 테이블을 참조하는 다른 테이블이 있는가?** → 반대쪽에 `@OneToMany`(또는 `@OneToOne`) + `mappedBy`로 매핑 (주인 아님)
3. **PK가 UNIQUE 제약까지 가진 FK인가?** (`AUTH_SESSION.user_id`, `GIFT_METADATA.message_id`처럼) → `@OneToOne`
4. **Fetch는 일단 전부 LAZY**
5. **Cascade는 부모-자식 생명주기가 진짜로 같이 가는 경우에만** (`User`-`Profile`처럼)

---

## 7. 엔티티 작성 진행 순서 (다음 단계)

1. FK가 없는 테이블부터 시작 (`USER`, `CHAT_ROOM`, `EMOJI`, `GIFT_PRODUCT` 등)
2. FK가 있는 테이블로 확장 (`PROFILE`, `AUTH_SESSION`, `FRIEND` ...)
3. 복잡한 관계(`ROOM_PARTICIPANT`처럼 FK가 여러 개인 것)는 마지막에
