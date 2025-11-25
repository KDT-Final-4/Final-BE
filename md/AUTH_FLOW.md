# 회원가입 · JWT 인증 · 권한 Flow 정리

이 문서는 `NGS` 백엔드에서 이메일/비밀번호와 OAuth2를 통해 회원을 등록하고 JWT로 인증·인가하는 전 과정을 코드 단위로 따라가면서 설명합니다. 타 프로젝트에서 동일한 패턴을 구현할 때 복사해 갈 수 있는 참조용 가이드로 작성했습니다.

## 1. Security 구성 개요
- **필터·세션 정책**: `SecurityConfig`는 `cors → csrf disable → headers → sessionManagement` 순으로 HTTP 보안을 설정하고, 세션을 `SessionCreationPolicy.STATELESS`로 고정하여 서버에 HttpSession을 남기지 않습니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:42`)
- **JWT 필터 배치**: 커스텀 `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 등록해 모든 요청에서 `Authorization` 헤더 또는 `ACCESS_TOKEN` 쿠키를 먼저 검사하도록 했습니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:39`)
- **엔드포인트 권한**: `/api/auth/**`, 게임/커뮤니티 등의 읽기 API, OAuth2 엔드포인트, Swagger는 `permitAll`, `/api/admin/**`는 `hasRole('ADMIN')`, `/api/publisher/**`는 `hasAnyRole('PUBLISHER','ADMIN')`, 나머지는 인증 필요로 선언되어 있습니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:54`)
- **비밀번호 정책**: `BCryptPasswordEncoder`가 Bean으로 등록되어 모든 가입/로그인/비밀번호 변경 시 동일한 해싱 전략을 사용합니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:34`)
- **메서드 수준 보안**: `@EnableMethodSecurity`가 켜져 있어서 각 컨트롤러에서 `@PreAuthorize("isAuthenticated()")` 같은 선언을 사용할 수 있습니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:24`)
- **OAuth2 설정**: 동일한 `SecurityFilterChain`에서 OAuth2 로그인 엔드포인트를 정의하고, AuthorizationRequest를 쿠키에 저장하는 저장소와 성공/실패 핸들러를 주입합니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:69`)

### 1.1 CORS 및 토큰 전달 방식
- `CorsConfig`는 `application.yml`의 `cors.*` 값을 그대로 Bean에 주입해 허용 Origin/Method/Header/SameSite 설정을 중앙집중화합니다. (`src/main/java/com/imfine/ngs/_global/config/cors/CorsConfig.java:30`, `src/main/java/com/imfine/ngs/_global/config/cors/CorsProperties.java:19`)
- `application.yml`에서 `allowed-origins`, `allow-credentials=true`를 명시했기 때문에 브라우저가 크로스 도메인에서도 `ACCESS_TOKEN` 쿠키를 주고받을 수 있습니다. (`src/main/resources/application.yml:65`)
- JWT 쿠키 속성( SameSite, Secure, Max-Age ) 역시 설정 파일로 조정하며 기본값은 `SameSite=None`, `Secure=true`, `Max-Age=21600(6h)`입니다. (`src/main/resources/application.yml:84`)

## 2. 회원 도메인과 기본 데이터
- `User` 엔터티는 이메일/비밀번호/이름/닉네임/프로필 URL/생년월일과 `UserRole`, `UserStatus`에 대한 다대일 연관관계를 가집니다. 비밀번호는 `pwd`, 닉네임은 미입력 시 이름으로 대체되고, `@PrePersist`에서 `createdAt`을 자동 등록합니다. (`src/main/java/com/imfine/ngs/user/entity/User.java:20`)
- `UserRole`과 `UserStatus`는 각각 역할 키와 상태명을 저장하며, 설명 필드를 가지고 있습니다. (`src/main/java/com/imfine/ngs/user/entity/UserRole.java:6`, `src/main/java/com/imfine/ngs/user/entity/UserStatus.java:6`)
- 애플리케이션 기동 시 `DefaultUserDataInitializer`가 `USER`, `PUBLISHER`, `ADMIN` 역할과 `ACTIVE` 상태를 DB에 보장합니다. (`src/main/java/com/imfine/ngs/user/config/DefaultUserDataInitializer.java:20`)
- `UserRepository`/`UserRoleRepository`/`UserStatusRepository`는 이메일 중복 여부, 기본 역할/상태 조회 등을 제공합니다. (`src/main/java/com/imfine/ngs/user/repository/UserRepository.java:9`, `src/main/java/com/imfine/ngs/user/repository/UserRoleRepository.java:9`, `src/main/java/com/imfine/ngs/user/repository/UserStatusRepository.java:9`)

## 3. 회원가입 Flow (이메일/비밀번호)
1. **요청 스펙**: `SignUpRequest`는 이메일/비밀번호/비밀번호 확인/이름/닉네임(선택)을 받으며, 각 필드는 `@NotBlank`, `@Email`, `@Size`로 검증됩니다. (`src/main/java/com/imfine/ngs/user/dto/request/SignUpRequest.java:11`)
2. **컨트롤러 진입**: `/api/auth/signup`은 `AuthController.signUp`에서 처리하고, 성공 시 `201 Created`만 반환합니다. 별도의 토큰은 이 시점에서 발급하지 않습니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:45`)
3. **서비스 로직**: `AuthService.signUp`은 이메일/이름/닉네임을 trim한 뒤 이메일 중복과 비밀번호 일치를 검사하고, 실패 시 `BusinessException`으로 오류 코드를 던집니다. (`src/main/java/com/imfine/ngs/user/service/AuthService.java:26`)
4. **비밀번호 암호화**: 전달받은 비밀번호는 `passwordEncoder.encode`로 해싱하여 `User.create` 팩토리에 전달됩니다. (`src/main/java/com/imfine/ngs/user/service/AuthService.java:38`)
5. **기본 권한/상태 부여**: 가입 시 `UserRoleRepository.findByRole("USER")`와 `UserStatusRepository.findByName("ACTIVE")`로 엔티티를 조회한 뒤 `user.assignRole/assignStatus`로 연관관계를 묶습니다. (`src/main/java/com/imfine/ngs/user/service/AuthService.java:44`)
6. **저장 후 응답**: 엔티티를 저장하면 끝이며, 프론트엔드는 이어서 `/signin`을 호출해야 JWT를 받습니다.
7. **부가 기능**: `/api/auth/email/check`는 이메일 중복 확인만 수행해 프론트에서 signup UX를 보조합니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:68`)

## 4. 로그인 & 토큰 발급 Flow
1. **요청 스펙**: `SignInRequest`는 이메일/비밀번호를 받습니다. (`src/main/java/com/imfine/ngs/user/dto/request/SignInRequest.java:10`)
2. **자격 증명 검증**: `AuthService.signIn`이 이메일로 사용자를 찾고(`findByEmail`), `passwordEncoder.matches`로 비밀번호를 비교합니다. 둘 중 하나라도 실패하면 단일 에러 코드 `AUTH_INVALID_CREDENTIALS`를 던집니다. (`src/main/java/com/imfine/ngs/user/service/AuthService.java:57`)
3. **JWT 생성**: 성공하면 `jwtUtil.generateToken(user.getId(), role)`을 호출하여 사용자 ID를 `subject`, 롤을 `role` 클레임으로 넣은 토큰을 발급합니다. (`src/main/java/com/imfine/ngs/user/service/AuthService.java:66`)
4. **응답 본문**: `SignInResponse`는 `accessToken`, `userId`, `email`, `nickname`을 JSON으로 돌려주어 프론트가 헤더 인증을 선택할 수 있습니다. (`src/main/java/com/imfine/ngs/user/dto/response/SignInResponse.java:8`)
5. **쿠키 발급**: 동시에 `AuthController.signIn`이 `ACCESS_TOKEN=<JWT>; Path=/; Max-Age=<설정값>; HttpOnly; SameSite=...; Secure` 형식의 쿠키를 `Set-Cookie` 헤더에 추가합니다. 이 속성 값은 `jwt.cookie.*` 프로퍼티를 그대로 참조하므로 배포 환경에 맞게 조절할 수 있습니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:51`)
6. **헤더 우선 순위**: 주석에서 알 수 있듯 Authorization 헤더의 Bearer 토큰이 들어오면 그것이 쿠키보다 우선합니다. 즉, 모바일/서버 간 통신은 헤더를, 브라우저 SPA는 HttpOnly 쿠키를 사용할 수 있습니다.

### 4.1 JWT 내용물
- `JwtUtil`은 HS256 서명 알고리즘과 애플리케이션 시크릿을 사용합니다. 만료 시간은 상수 `EXPIRATION_MS = 6시간`으로 고정되어 있습니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUtil.java:18`)
- `generateToken`은 ID를 문자열로 `subject`에 넣고, 역할이 존재하면 `role` 클레임을 추가합니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUtil.java:29`)
- `getUserIdFromToken`은 토큰을 파싱한 뒤 `claims.getSubject()`를 `Long`으로 변환해 Controller 계층에서 사용할 ID를 제공합니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUtil.java:40`)
- `getRoleFromToken`은 선택적으로 role 클레임을 읽어 인가 시 사용합니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUtil.java:58`)

## 5. JWT 인증 필터 & SecurityContext
1. **토큰 추출**: `JwtAuthenticationFilter`는 먼저 `Authorization` 헤더에서 `Bearer ` 접두사를 제거해 토큰을 얻고, 없다면 `ACCESS_TOKEN` 쿠키를 순회하며 값을 찾습니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtAuthenticationFilter.java:32`)
2. **유효성 검사**: SecurityContext에 아직 Authentication이 없고 `jwtUtil.isValidToken(token)`이 true일 때만 후속 처리를 진행합니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtAuthenticationFilter.java:49`)
3. **Principal 구성**: 토큰에서 userId/role을 뽑아 `JwtUserPrincipal`을 만들고, 이를 담은 `UsernamePasswordAuthenticationToken`을 SecurityContext에 저장합니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtAuthenticationFilter.java:50`, `src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUserPrincipal.java:15`)
4. **권한 부여 정보**: `JwtUserPrincipal.getAuthorities`는 role 문자열 앞에 `ROLE_`을 붙여 Spring Security 권한으로 변환합니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUserPrincipal.java:24`)
5. **세션-less 동작**: `SessionCreationPolicy.STATELESS` 덕분에 이 Authentication은 요청 사이에 서버 메모리에 남지 않고, 매 요청마다 필터가 다시 토큰을 검증해 SecurityContext를 채웁니다. 따라서 “세션을 어디에 저장하느냐”라는 질문에는 “저장하지 않고 매 요청마다 JWT로 다시 만든다”가 답입니다.

## 6. 인증 정보 활용 예시
- **나의 정보 조회**: `/api/auth/me`는 `@PreAuthorize("isAuthenticated()")`와 `@AuthenticationPrincipal JwtUserPrincipal`을 사용해 로그인을 요구하고, SecurityContext에서 꺼낸 userId로 실제 `User` 엔티티를 조회해 응답을 빌드합니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:87`)
- **비밀번호 변경**: `/api/auth/updatePwd` 역시 현재 Principal의 `userId`를 전달해 서비스에서 기존 비밀번호 검증 → 새 비밀번호 해싱 → 저장 과정을 수행합니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:76`, `src/main/java/com/imfine/ngs/user/service/AuthService.java:71`)
- **SecurityContext 흐름**: ➊ 클라이언트가 Authorization 헤더나 쿠키를 보낸다 → ➋ `JwtAuthenticationFilter`가 토큰을 검증하고 `JwtUserPrincipal(userId, role)`을 만들어 Context에 넣는다 → ➌ 컨트롤러는 `@AuthenticationPrincipal`이나 `SecurityContextHolder`로 userId/role을 읽어 도메인 로직을 실행한다.

## 7. OAuth2 로그인 Flow
1. **AuthorizationRequest 저장**: OAuth2 로그인 시 Spring Security는 `CookieAuthorizationRequestRepository`를 사용해 직렬화된 AuthorizationRequest와 redirect_uri를 HttpOnly 쿠키 `OAUTH2_AUTH_REQUEST`, `redirect_uri`에 저장합니다. (`src/main/java/com/imfine/ngs/user/oauth/CookieAuthorizationRequestRepository.java:15`)
2. **성공 핸들러 진입**: 프로바이더 인증이 끝나면 `OAuth2AuthenticationSuccessHandler`가 호출되며, provider 별 attribute 구조(google/kakao/naver)에서 이메일과 이름을 추출합니다. (`src/main/java/com/imfine/ngs/user/oauth/OAuth2AuthenticationSuccessHandler.java:44`)
3. **필수 정보 보정**: 이메일이 비어 있으면 OAuth2 쿠키를 삭제하고 클라이언트에 `?error=email_not_provided_<provider>`로 리다이렉트하여 명확히 실패를 알립니다. (`src/main/java/com/imfine/ngs/user/oauth/OAuth2AuthenticationSuccessHandler.java:86`)
4. **회원 upsert**: 정상일 경우 `SocialService.upsertSocialUserWithRole`가 이메일 기준으로 사용자를 찾거나 `User.create`를 이용해 `SOCIAL` 비밀번호, 기본 역할/상태로 새로 만듭니다. (`src/main/java/com/imfine/ngs/user/service/SocialService.java:55`)
5. **JWT 발급/쿠키 저장**: 소셜 사용자 ID와 역할로 `jwtUtil.generateToken`을 호출하고, 로컬 로그인과 동일한 규칙으로 `ACCESS_TOKEN` HttpOnly 쿠키를 내려줍니다. (`src/main/java/com/imfine/ngs/user/oauth/OAuth2AuthenticationSuccessHandler.java:100`)
6. **리다이렉트**: 최종적으로 `app.oauth2.redirect-url`(기본값 `http://localhost:3000/auth/callback`)로 302 응답을 보내고, AuthorizationRequest 쿠키를 정리합니다. (`src/main/java/com/imfine/ngs/user/oauth/OAuth2AuthenticationSuccessHandler.java:106`)

## 8. 로그아웃과 세션 정리
- `/api/auth/signout`은 SecurityContext를 비우고, `ACCESS_TOKEN` 쿠키를 `Max-Age=0`으로 재발행해 즉시 만료시키며, OAuth2 Authorization 쿠키까지 제거합니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:106`, `src/main/java/com/imfine/ngs/user/oauth/CookieAuthorizationRequestRepository.java:77`)
- 동일 동작을 `GET /api/auth/signout`에서도 허용해 브라우저가 간단히 호출할 수 있습니다. (`src/main/java/com/imfine/ngs/user/controller/AuthController.java:129`)
- 서버 세션이 없으므로 로그아웃은 “쿠키 삭제 + SecurityContextHolder.clearContext”로 충분합니다.

## 9. 권한 부여 포인트 정리
- `JwtUserPrincipal`이 role을 `ROLE_` prefix 권한으로 변환해 놓기 때문에, 컨트롤러에서는 `@PreAuthorize("hasRole('ADMIN')")` 같은 선언을 자유롭게 사용할 수 있습니다. (`src/main/java/com/imfine/ngs/_global/config/security/jwt/JwtUserPrincipal.java:24`)
- URL 단위 접근 제어는 SecurityConfig의 `requestMatchers`로 처리하고, 세부 API는 `@PreAuthorize("isAuthenticated()")` 등으로 이중 잠금을 거는 패턴입니다. (`src/main/java/com/imfine/ngs/_global/config/security/SecurityConfig.java:54`)

## 10. Flow 요약 & 타 프로젝트 적용 체크리스트
1. **회원가입**: `SignUpRequest` → 중복/비밀번호 검증 → BCrypt 해싱 → 기본 역할/상태 부여 → 저장만 하고 끝. (토큰 없음)
2. **로그인**: 이메일/비밀번호 검증 → `jwtUtil.generateToken(userId, role)` → JSON + `ACCESS_TOKEN` 쿠키 동시 발급.
3. **인증된 API 호출**: 클라이언트가 `Authorization: Bearer <JWT>` 또는 HttpOnly 쿠키를 전송 → `JwtAuthenticationFilter`가 토큰에서 `userId`를 파싱 (`JwtUtil.getUserIdFromToken`)하고 SecurityContext에 `JwtUserPrincipal`을 올림 → 컨트롤러는 `principal.getUserId()`로 실제 사용자 행위를 실행.
4. **OAuth2 로그인**: AuthorizationRequest를 HttpOnly 쿠키에 저장 → 콜백에서 `OAuth2AuthenticationSuccessHandler`가 사용자 정보 확보 → `SocialService`로 upsert → 동일한 JWT/쿠키 발급 → 프론트 리다이렉트.
5. **세션 관리**: 서버는 상태를 들고 있지 않으므로, 세션 저장소 구성 없이 JWT만으로 인증 상태를 재구성합니다. 로그아웃은 쿠키 제거가 전부입니다.
6. **권한 체크**: JWT에 role 클레임을 담았으므로 `JwtUserPrincipal`의 `getAuthorities`가 곧바로 URL/메서드 인가에 활용됩니다.

이 구조를 그대로 복제하면 “세션 저장 없이 JWT만으로 인증/인가를 처리하고, 브라우저와 서버 모두가 사용할 수 있는 이중 채널(헤더+HttpOnly 쿠키)”을 구현할 수 있습니다. 필요한 부분은 위에 명시한 소스 파일과 라인을 참고해 맞춤 수정하면 됩니다.
