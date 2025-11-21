# 회원가입 & 로그인 흐름

## 엔드포인트 개요
- **URL / Method**: `POST /api/user/register`
- **책임**: `UserRegisterRequest`를 받아 BCrypt로 비밀번호를 암호화하고 `UserMapper`를 통해 저장하며, 성공/실패 메시지(또는 단순 상태 코드)를 응답으로 돌려줍니다. 필요하다면 추가 DTO로 생성된 계정 정보를 내려도 됩니다.
- **관련 컴포넌트**:
  - `UserRegisterRequest` (도메인 `User` 생성)
  - `UserController` (HTTP 진입점)
  - `UserService` (비즈니스 로직 + 비밀번호 암호화)
  - `UserMapper` (MyBatis 매퍼)
  - `BCryptPasswordEncoder` 빈 (비밀번호 해시/검증)

## 상세 흐름
1. **컨트롤러** (`UserController.register`)
   - `@PostMapping("/api/user/register")`로 노출합니다.
   - `@Valid` 등을 사용해 `UserRegisterRequest`의 이메일/비밀번호/이름을 검증합니다.
   - `UserService.register`에 처리를 위임합니다.
   - 서비스가 반환한 도메인 객체나 DTO를 HTTP 201 응답(필요 시 `Location` 헤더)으로 매핑합니다.

2. **서비스** (`UserService`)
   - `_core/config`에 설정한 `BCryptPasswordEncoder` 빈과 `UserMapper`를 주입받습니다.
   - `passwordEncoder.encode(request.getPassword())`로 평문 비밀번호를 암호화합니다.
   - `request.toEntity()`로 생성한 도메인 객체의 비밀번호를 암호화된 값으로 덮어쓴 뒤 저장합니다(빌더 복사 or `request.toEntityWithPassword(String encodedPassword)`와 같은 헬퍼 사용).
   - 필요하면 `userMapper.findByEmail`로 중복 이메일을 검사합니다.
  - `userMapper.insert(user)`로 저장하고, 성공 메시지 혹은 DTO(`UserInfoMapper`)를 선택적으로 만들어 반환합니다.

3. **영속 계층** (`UserMapper`)
   - `insert(User user)`가 해시된 비밀번호를 포함해 사용자 레코드를 작성합니다.
   - `findByEmail`은 회원가입 중복 검사와 로그인 시 사용자 조회에 재활용합니다.

4. **트랜잭션 / 예외 처리**
   - 중복 이메일이면 도메인 예외를 던지거나 HTTP 409를 반환합니다.
   - 이후 다른 테이블까지 확장되면 회원가입 메서드를 트랜잭션으로 감쌉니다.

## 레이어별 애너테이션 & 코드 스케치
아래 템플릿을 참고해서 계층별 구성 요소를 작성하면 됩니다.

### DTO (`UserRegisterRequest`)
```java
@Slf4j
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRegisterRequest {
  @Email
  @NotBlank
  private String email;

  @NotBlank
  @Size(min = 8, max = 30)
  private String password;

  @NotBlank
  private String name;

  @Builder.Default
  private Long roleId = Role.defaultRoleId();

  public User toEntity() {
    // 기존 로직 유지 (createdAt, Role 매핑 등)
  }
}
```
- 유효성 검증: `@Email`, `@NotBlank`, `@Size`.
- 로깅: `@Slf4j`로 요청 처리 상황을 추적하되 비밀번호는 로그에 남기지 않습니다.

### 컨트롤러
```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse> register(
      @Valid @RequestBody UserRegisterRequest request) {
    userService.register(request);
    ApiResponse body = ApiResponse.of("회원가입이 완료되었습니다.");
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }
}
```
- `@RestController` + `@RequestMapping`으로 베이스 경로 선언.
- `@RequiredArgsConstructor`로 서비스 주입.
- `@Valid @RequestBody`로 DTO 검증 및 JSON 바인딩.
- 성공 시 201 상태 코드만 내려도 충분하며, 예시처럼 커스텀 메시지 DTO(`ApiResponse`)를 같이 내려도 됩니다. 실패 시 예외 핸들러(예: `@RestControllerAdvice`)에서 400/409 등을 처리합니다.

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
  public void register(UserRegisterRequest request) {
    ensureEmailAvailable(request.getEmail());
    String encoded = passwordEncoder.encode(request.getPassword());

    User user = request.toEntity().toBuilder()
        .password(encoded)
        .build();

    userMapper.insert(user);
    // 필요 시 userInfoMapper.toUserInfo(user)를 이용해 반환 DTO 구성
  }

  private void ensureEmailAvailable(String email) {
    if (userMapper.findByEmail(email) != null) {
      throw new DuplicateUserException(email);
    }
  }
}
```
- 클래스 레벨 `@Transactional(readOnly = true)` + 메서드 레벨 `@Transactional`로 쓰기 트랜잭션을 명시.
- `PasswordEncoder` 주입 후 `encode` 결과를 엔터티에 반영.
- 중복 이메일 검사 메서드에서 커스텀 예외를 던져 컨트롤러 어드바이스에서 409로 응답.

### 매퍼 (`UserMapper.xml` 예시)
```xml
<insert id="insert" parameterType="com.final_team4.finalbe.user.domain.User">
  INSERT INTO users
    (email, password, name, role_id, created_at, updated_at, deleted, prompt, llm_publisher)
  VALUES
    (#{email}, #{password}, #{name}, #{roleId}, #{createdAt}, #{updatedAt}, #{deleted},
     #{prompt}, #{llmPublisher})
</insert>
```
- 인터페이스에는 `@Mapper`를 붙였으므로 XML이나 애너테이션 쿼리 중 선택.
- 로그인 시 재사용할 `findByEmail`도 같은 XML에 정의 (`SELECT * FROM users WHERE email = #{email} AND deleted = 0`).

### 로그인 서비스 스케치
```java
public TokenResponse login(LoginRequest request) {
  User user = Optional.ofNullable(userMapper.findByEmail(request.getEmail()))
      .orElseThrow(() -> new UnauthorizedException("잘못된 인증 정보"));

  if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    throw new UnauthorizedException("잘못된 인증 정보");
  }

  return tokenProvider.issue(user);
}
```
- 회원가입과 동일한 `PasswordEncoder` 빈 사용.
- 실패 시 동일한 메시지를 내려서 정보 노출을 막습니다.
- 로그인은 성공 시 토큰/세션 정보를 반환하지만, 회원가입은 단순 메시지나 201 상태 코드만으로도 충분합니다.

## 비밀번호 암호화 & 검증
- **빈 설정**:
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
  ```
- **회원가입**:
  ```java
  public void register(UserRegisterRequest request) {
      validateEmailNotDuplicated(request.getEmail());
      String encoded = passwordEncoder.encode(request.getPassword());
      User user = request.toEntity().toBuilder().password(encoded).build();
      userMapper.insert(user);
      // 필요 시 return userInfoMapper.toUserInfo(user);
  }
  ```
- **로그인 검증** (`UserService.login`):
  ```java
  public UserInfo login(String email, String rawPassword) {
      User user = Optional.ofNullable(userMapper.findByEmail(email))
          .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
      if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
          throw new UnauthorizedException("Invalid credentials");
      }
      return userInfoMapper.toUserInfo(user);
  }
  ```
- `BCryptPasswordEncoder.matches(raw, encoded)`가 평문 비밀번호와 저장된 해시를 안전하게 비교하므로, 최초 요청을 처리한 뒤에는 평문을 보관하거나 전송하지 않습니다.

## 시퀀스 요약
1. 클라이언트 → `UserController`에 JSON 요청 전송
2. 컨트롤러 → `UserService.register` 호출
3. 서비스가 비밀번호를 BCrypt로 암호화하고 `User` 생성
4. 서비스 → `UserMapper.insert` 실행
5. 매퍼가 DB에 저장하고 반영된 행 수를 반환
6. 서비스가 응답 DTO로 변환해 컨트롤러에 전달
7. 컨트롤러가 생성된 사용자 정보(비밀번호 제외)를 담아 HTTP 201/200 응답 반환

## 참고
- `UserRegisterRequest.password`에는 검증 애노테이션을 적용하고, `@Slf4j` 로그에는 평문이 남지 않도록 주의합니다.
- 로그인/토큰 발급 시에도 동일한 `PasswordEncoder` 빈을 재사용해 암호화 파라미터 차이를 방지합니다.
- BCrypt 비용(Strength)을 조정하려면 설정 값을 통해 관리하면 코드 변경 없이도 작업량을 튜닝할 수 있습니다.
- 위 예시는 이미 존재하는 `UserInfo` DTO를 사용합니다. 다른 응답 스펙이 필요하면 `UserInfoResponse` 같은 클래스를 추가로 만들어 `UserInfo`를 감싸거나 필드를 확장하세요.
- 회원가입 응답은 `201 Created`만 내려도 REST 규약에 부합합니다. 클라이언트에서 성공 여부 텍스트를 요구한다면 `ApiResponse`, `SimpleMessageResponse` 같은 공용 DTO를 만들어 메시지를 담아주면 됩니다. 신규 유저 정보를 즉시 내려야 한다면 `UserInfo` 또는 확장 DTO를 사용하세요.
