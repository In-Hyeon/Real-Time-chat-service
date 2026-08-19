# 엔티티에서 사용한 import 정리

`User`, `ChatRoom`, `Emoji`, `GiftProduct` 엔티티를 작성하며 사용한 import들을 출처별로 정리한 문서입니다. 새 엔티티를 작성할 때 참고용으로 씁니다.

## 1. `jakarta.persistence.*` — JPA 표준 API

`build.gradle`의 `spring-boot-starter-data-jpa` 의존성이 가져오는 **JPA(Jakarta Persistence API) 표준 명세**입니다. 자바 객체를 테이블에 매핑하는 방법을 정의한 표준 규격이고, 실제 동작(SQL 생성 등)은 이 표준을 구현한 **Hibernate**(같은 의존성이 함께 가져옴)가 처리합니다.

| import | 역할 |
|---|---|
| `Entity` | 이 클래스가 DB 테이블과 매핑되는 클래스임을 선언 |
| `Table` | 매핑될 실제 테이블 이름을 지정 (`@Table(name = "USER")`) — 없으면 클래스명을 그대로 테이블명으로 씀 |
| `Id` | 이 필드가 PK(기본키)임을 선언 |
| `GeneratedValue` | PK 값을 누가 생성하는지 전략을 지정 |
| `GenerationType` | `GeneratedValue`에 넘길 전략 값들의 열거형 |
| `Column` | 필드와 실제 컬럼을 매핑, `name`/`nullable`/`unique`/`length`/`precision`/`scale` 같은 제약조건 지정 |

### `GenerationType` 전략 상세

PK 값을 누가, 어떻게 만드는지에 대한 4가지 선택지입니다.

| 전략 | 값을 만드는 주체 | 동작 방식 |
|---|---|---|
| `IDENTITY` | **DB 자체** | DB의 auto-increment 컬럼 기능에 완전히 위임 |
| `SEQUENCE` | **DB의 시퀀스 객체** | INSERT 전에 미리 "다음 번호 주세요"라고 물어봄 |
| `TABLE` | **DB의 별도 카운터 테이블** | 시퀀스를 흉내내려고 카운터 전용 테이블을 하나 더 두고 관리 |
| `AUTO` | JPA 구현체(Hibernate)가 알아서 결정 | DB 종류를 보고 위 셋 중 하나를 자동 선택 |

**왜 우리는 `IDENTITY`를 쓰는가**

DDL에서 PK 컬럼은 이렇게 정의되어 있습니다.

```sql
`id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT
```

`AUTO_INCREMENT`는 MySQL이 자체적으로 제공하는 "이 컬럼 값은 DB가 알아서 1씩 증가시켜서 채워준다"는 기능입니다. `GenerationType.IDENTITY`를 쓰면 Hibernate는 id 값을 만들지 않고, INSERT 문에 id 컬럼을 아예 빼고 보낸 뒤 MySQL이 auto_increment로 채운 값을 돌려받습니다. 즉 **DB의 AUTO_INCREMENT 기능과 JPA의 IDENTITY 전략이 1:1로 대응**됩니다.

`SEQUENCE`는 Oracle/PostgreSQL처럼 독립된 시퀀스 객체가 있는 DB에서 쓰는 방식인데, MySQL은 전통적으로 시퀀스 객체가 없고 `AUTO_INCREMENT` 컬럼 속성만 있어서 이 전략을 쓸 수 없습니다.

**실전에서 체감되는 차이**

- `SEQUENCE`는 INSERT 전에 미리 번호표를 받을 수 있어 여러 INSERT를 모아 보내는 배치(batch) 최적화가 가능합니다.
- `IDENTITY`는 실제로 INSERT가 DB에 실행되고 나서야 값이 정해지므로 미리 알 방법이 없고, 배치 INSERT 최적화를 못 씁니다.
- 코드 레벨에서는, `save()` 호출 전까지 `id`는 `null`이고, DB에 실제 INSERT가 실행된 후에야 값이 채워집니다.

```java
User user = User.create("a@a.com", "1234", "+8210...");
System.out.println(user.getId()); // null (아직 DB에 저장 안 됐으니까)

userRepository.save(user);
System.out.println(user.getId()); // 이제서야 값이 채워짐 (예: 1)
```

## 2. `lombok.*` — Lombok 라이브러리

JPA와는 별개로, `build.gradle`에 `compileOnly 'org.projectlombok:lombok'` + `annotationProcessor 'org.projectlombok:lombok'`로 추가된 **반복 코드 자동 생성 도구**입니다. 컴파일 시점에 getter, 생성자 같은 뻔한 코드를 어노테이션 하나로 만들어줍니다.

| import | 역할 |
|---|---|
| `Getter` | 모든 필드에 대해 `getXxx()` 메서드를 자동 생성 |
| `NoArgsConstructor` | 파라미터 없는 생성자를 자동 생성 |
| `AccessLevel` | `NoArgsConstructor`의 접근 제어자를 지정할 때 쓰는 열거형 (`AccessLevel.PROTECTED`로 JPA만 쓸 수 있게 제한) |

엔티티에 `@Setter`나 `@Data`를 쓰지 않는 이유: 어디서든 값을 마음대로 바꿀 수 있게 되면 "왜 값이 바뀌었는지" 추적이 힘들어지기 때문에, 조회는 `@Getter`로만 열어두고 의미 있는 변경은 목적이 드러나는 메서드(`changePassword()` 등)로 따로 만듭니다.

## 3. `java.time.LocalDateTime`, `java.math.BigDecimal` — JDK 표준 라이브러리

별도 의존성 없이 JDK에 기본 내장된 클래스입니다.

- `LocalDateTime` — DB의 `DATETIME` 컬럼과 매핑 (`created_at` 등). 시간대 정보 없는 날짜+시간.
- `BigDecimal` — DB의 `DECIMAL(10,2)` 같은 정밀한 소수(돈 계산 등)와 매핑. `double`을 쓰면 부동소수점 오차가 생길 수 있어서 금액 컬럼에는 항상 `BigDecimal`을 씁니다.

## 정리

| 그룹 | 역할 | 출처 |
|---|---|---|
| `jakarta.persistence.*` | DB와 어떻게 매핑할지 정의 (JPA 표준) | `spring-boot-starter-data-jpa` 의존성 |
| `lombok.*` | 반복되는 자바 문법을 줄여주는 코드 생성 도구 | `lombok` 의존성 |
| `java.time`, `java.math` | 그냥 자바 표준 타입 | JDK 내장 (의존성 불필요) |
