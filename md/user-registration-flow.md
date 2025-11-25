# 회원가입 & 로그인 흐름

## 엔드포인트 개요
- **URL / Method**: `POST /api/user/register`
- **입력 필드**: `email`, `password`, `name`만 받습니다. `roleId`는 요청에 두지 않으며, 서비스가 기본 역할(ROLE_USER, id=1)로 덮어씁니다.
- **책임**: 입력을 검증한 뒤 비밀번호를 BCrypt로 암호화하고 `UserMapper`로 저장, 공통 응답(`ApiResponse<UserSummaryResponse>`)으로 성공 메시지와 사용자 요약 정보를 반환합니다.
- **관련 컴포넌트**:
  - `UserRegisterRequestDto` (도메인 `User` 생성 + 기본 역할/타임스탬프 세팅)
  - `UserController` (HTTP 진입점, 공통 응답 포맷 적용)
  - `UserService` (비즈니스 로직 + 비밀번호 암호화 + 중복 검증)
  - `UserMapper` / `UserMapper.xml` (MyBatis로 DB 접근, Oracle은 시퀀스/IDENTITY에 맞춰 키 회수 설정 필요)
  - `UserInfoMapper` (도메인 → 요약 DTO 변환)
  - `PasswordEncoder` 빈 (`BCryptPasswordEncoder`)
  - DB 선행조건: `ROLES`(또는 `ROLE`) 테이블에 `ID=1, NAME='ROLE_USER'`가 존재해야 FK가 만족됩니다.

## 공통 응답 규격
```json
{
  "success": true,
  "message": "회원가입이 완료됐습니다.",
  "data": {
    "userId": 1,
    "email": "codex@example.com",
    "nickname": "codex"
  },
  "timestamp": "2024-07-25T09:20:00.123"
}
```
- `ApiResponse<T>`는 `(success, message, data, timestamp)` 4개 필드로 고정되어 있으며, 회원가입 시 `data` 자리에 `UserSummaryResponse`를 넣습니다.
- 로그인 시에도 `ResponseEntity<ApiResponse<AuthLoginResponse>>` 형태로 동일 래퍼를 재사용하고, `data.user`/`data.token`에 각각 사용자·토큰 정보를 담는 것을 목표로 합니다.
- 실패 응답 시에는 `ApiResponse.fail(message, null)` 형태로 내려주고 HTTP 상태 코드는 `@RestControllerAdvice`에서 예외 종류별로 매핑합니다.

## 도메인 DTO & 응답 모델

### UserRegisterRequestDto
```java
@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDto {
  @Email
  @NotBlank
  private String email;

  @NotBlank
  @Size(min = 3, max = 30)
  private String password;

  @NotBlank
  private String name;

  public User toEntity() {
    LocalDateTime now = LocalDateTime.now();
    RoleType roleType = RoleType.USER;          // 기본 역할 고정
    Role role = Role.from(roleType);            // id=1, name=ROLE_USER

    return User.builder()
        .email(email)
        .password(password) // 서비스에서 암호화된 값으로 덮어씀
        .name(name)
        .roleId(role.getId())
        .role(role)
        .createdAt(now)
        .updatedAt(now)
        .isDelete(0)
        .build();
  }
}
```
- 요청 스펙에 `roleId`가 없습니다. 기본 역할은 DTO→엔티티 변환 시에 고정됩니다.
- `isDelete`는 신규 가입 시 0으로 초기화하며 DB 컬럼 `IS_DELETE`와 매핑됩니다.

### UserSummaryResponse
```java
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSummaryResponse {
  private Long userId;
  private String email;
  private String nickname;
}
```
- `UserInfoMapper.toUserSummary(User user)`에서 도메인 객체를 이 DTO로 변환합니다.
- 로그인 응답에서는 `nickname` 자리에 동일한 이름 값을 재사용하거나, 추후 별도의 닉네임 컬럼이 생기면 해당 값을 매핑합니다.

### ApiResponse<T>
```java
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp
) {
  public static <T> ApiResponse<T> ok(String message, T data) {
    return new ApiResponse<>(true, message, data, LocalDateTime.now());
  }

  public static ApiResponse<Void> ok(String message) {
    return ok(message, null);
  }

  public static <T> ApiResponse<T> fail(String message, T data) {
    return new ApiResponse<>(false, message, data, LocalDateTime.now());
  }
}
```
- 모든 REST 응답을 이 구조로 통일하면 프런트에서 `success`/`message`/`data`만 확인하면 되므로 상태 관리가 단순해집니다.

## 상세 흐름
1. **컨트롤러 (`UserController.register`)**
   - `@PostMapping("/api/user/register")`와 `@Valid`로 HTTP 요청을 검증합니다.
   - `UserService.register` 호출 후 돌려받은 `UserSummaryResponse`를 `ApiResponse.ok("회원가입이 완료됐습니다.", summary)`로 감싼 뒤 200 OK로 반환합니다.
   - 동일한 응답 래퍼를 로그인/토큰 API에서도 사용하기 위해 컨트롤러 레벨에서 응답 구조를 고정합니다.

2. **서비스 (`UserService.register`)**
   - `ensureEmailAvailable`로 중복 이메일을 검사하고, 존재하면 `RuntimeException`(향후 커스텀 예외)으로 차단합니다.
   - `PasswordEncoder.encode`로 평문 비밀번호를 암호화합니다.
   - DTO가 생성한 `User` 엔티티를 `toBuilder()`로 복사해 암호화된 비밀번호를 주입합니다.
   - `userMapper.insert(user)` 호출 시 Oracle에 맞게 PK를 세팅해야 합니다. (아래 매퍼 설정 참고)
   - 저장된 `User`를 `userInfoMapper.toUserSummary(user)`로 변환하여 컨트롤러에 전달합니다.

3. **영속 계층 (`UserMapper` + XML)**
   - `UserMapper.insert(User user)`는 `IS_DELETE` 컬럼까지 포함해 신규 레코드를 생성합니다. Oracle에서는 다음 둘 중 하나로 PK를 채웁니다.
     - 시퀀스 사용: `<selectKey keyProperty="id" resultType="long" order="BEFORE">SELECT USERS_SEQ.NEXTVAL FROM dual</selectKey>`
     - IDENTITY 컬럼 사용 시: `useGeneratedKeys="true"` + `keyColumn="ID"` (ROWID가 아닌 PK 컬럼을 읽어오도록 강제)
   - `findByEmail`은 중복 검증과 로그인 조회에 공용으로 사용합니다.
   - `UserMapper.xml`에서 resultMap과 insert 구문 모두 `IS_DELETE ↔ isDelete` 매핑을 사용해야 실제 DB 스키마와 일치합니다.

4. **트랜잭션 / 예외 처리**
   - 클래스 레벨 `@Transactional(readOnly = true)` + 메서드 레벨 `@Transactional` 조합으로 쓰기 작업 시 트랜잭션을 시작합니다.
   - 중복 이메일, 비밀번호 정책 위반 등은 `@RestControllerAdvice`에서 잡아 공통 에러 응답(`ApiResponse.fail`)으로 내려줍니다.

## 레이어별 코드 스케치

### 컨트롤러
```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserSummaryResponse>> register(
      @Valid @RequestBody UserRegisterRequestDto request) {
    UserSummaryResponse createdUser = userService.register(request);
    return ResponseEntity.ok(ApiResponse.ok("회원가입이 완료됐습니다.", createdUser));
  }
}
```

### 서비스
```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final UserInfoMapper userInfoMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserSummaryResponse register(UserRegisterRequestDto request) {
    ensureEmailAvailable(request.getEmail());

    User user = request.toEntity().toBuilder()
        .password(passwordEncoder.encode(request.getPassword()))
        .build();

    userMapper.insert(user);
    return userInfoMapper.toUserSummary(user);
  }

  private void ensureEmailAvailable(String email) {
    if (userMapper.findByEmail(email) != null) {
      throw new RuntimeException("이미 존재하는 이메일: " + email);
    }
  }
}
```

### MyBatis 매퍼
```xml
<resultMap id="UserResultMap" type="com.final_team4.finalbe.user.domain.User">
  <id column="ID" property="id"/>
  <result column="ROLE_ID" property="roleId"/>
  <result column="NAME" property="name"/>
  <result column="EMAIL" property="email"/>
  <result column="PASSWORD" property="password"/>
  <result column="CREATED_AT" property="createdAt"/>
  <result column="UPDATED_AT" property="updatedAt"/>
  <result column="IS_DELETE" property="isDelete"/>
</resultMap>

<insert id="insert" parameterType="com.final_team4.finalbe.user.domain.User">
  <!-- Oracle 예시: 시퀀스로 PK를 먼저 채움 -->
  <selectKey keyProperty="id" resultType="long" order="BEFORE">
    SELECT USERS_SEQ.NEXTVAL FROM dual
  </selectKey>
  INSERT INTO USERS (
      ROLE_ID,
      NAME,
      EMAIL,
      PASSWORD,
      CREATED_AT,
      UPDATED_AT,
      IS_DELETE
  ) VALUES (
      #{roleId},
      #{name},
      #{email},
      #{password},
      #{createdAt},
      #{updatedAt},
      #{isDelete}
  )
</insert>
```
- IDENTITY 컬럼을 사용한다면 `<insert ... useGeneratedKeys="true" keyProperty="id" keyColumn="ID">`로 대체할 수 있습니다.
- `BaseColumns` 등을 정의해 `findById`, `findByEmail`에서도 동일한 컬럼 목록을 재사용합니다.
- 로그인에서 soft delete를 고려하려면 `WHERE EMAIL = #{email} AND IS_DELETE = 0` 조건을 기본으로 두는 것이 안전합니다.

## 로그인 확장 로드맵
1. **요청 DTO**: `AuthLoginRequest(email, password)`를 만들고 `@Valid`로 검증합니다.
2. **서비스 로직**:
   ```java
   public AuthLoginResponse login(AuthLoginRequest request) {
     User user = Optional.ofNullable(userMapper.findByEmail(request.email()))
         .orElseThrow(() -> new UnauthorizedException("잘못된 인증 정보"));
     if (!passwordEncoder.matches(request.password(), user.getPassword())) {
       throw new UnauthorizedException("잘못된 인증 정보");
     }
     JwtToken token = jwtTokenService.issueToken(user.getEmail(), List.of(user.getRoleName()));
     return AuthLoginResponse.of(
         user.getId(),
         user.getEmail(),
         user.getName(),
         user.getRole().getName(),
         token.value(),
         token.issuedAt(),
         token.roles());
   }
   ```
3. **컨트롤러 응답**: `ResponseEntity.ok(ApiResponse.ok("로그인이 완료됐습니다.", authLoginResponse))`.
4. **프런트 연동**: 회원가입과 로그인 모두 동일한 `success/message/data/timestamp` 구조만 검사하면 되므로 상태 관리가 단순해집니다.

## 비밀번호 암호화 & 검증
- **빈 설정**:
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  ```
- **회원가입 시 사용**:
  ```java
  String encoded = passwordEncoder.encode(request.getPassword());
  User user = request.toEntity().toBuilder().password(encoded).build();
  userMapper.insert(user);
  ```
- **로그인 시 검증**:
  ```java
  if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    throw new UnauthorizedException("잘못된 인증 정보");
  }
  ```
- BCrypt는 입력마다 다른 솔트를 포함하므로 DB에는 암호화 문자열만 저장하면 됩니다.

## 시퀀스 요약
1. 클라이언트가 `/api/user/register`에 JSON을 전송한다.
2. `UserController`가 DTO 검증 후 `UserService.register`를 호출한다.
3. 서비스가 비밀번호를 BCrypt로 암호화하고 `UserMapper.insert`로 저장한다.
4. Oracle에서는 시퀀스(`<selectKey>`) 또는 `useGeneratedKeys + keyColumn="ID"`로 PK를 채워 `User.id`를 완성한다.
5. 서비스가 `UserSummaryResponse`로 변환해 컨트롤러에 돌려준다.
6. 컨트롤러가 `ApiResponse.ok`를 사용해 HTTP 200 응답을 완성한다.
7. 로그인 API를 추가할 때도 동일한 응답 래퍼를 사용하므로 클라이언트 처리가 일관된다.

## 참고
- DTO/서비스/매퍼 모두 패키지 경로를 `src/main/java/com/final_team4/finalbe/user/**` 체계에 맞춰 배치합니다.
- `UserRegisterRequestDto`에는 `@Slf4j`를 붙였지만 민감한 값(비밀번호)은 로그에 남기지 않습니다.
- 이메일 중복 예외는 현재 `RuntimeException`으로 처리하고 있으며, 향후 `DuplicateUserException`으로 교체 후 `@RestControllerAdvice`에서 409를 반환하도록 확장할 예정입니다.
- 로그인 구현 시 `AuthLoginResponse` + `TokenInfo` 구조를 재사용하면 토큰 만료 시각, 롤 목록 등을 한 번에 내려줄 수 있습니다.
- Oracle에서 `ORA-17132` / `ROWID` 관련 예외가 날 경우, 위의 `<selectKey>`(시퀀스) 또는 `keyColumn="ID"` 설정을 적용해야 합니다.
- 테스트는 `@SpringBootTest`보다 `@DataJpaTest`/`@MybatisTest` 조합 또는 서비스 단위 테스트로 가볍게 유지하며, PR 전 `./gradlew test` 실행을 권장합니다.
