# Final-BE 실행 방법

1. `docker compose up -d`를 실행하면 Oracle DB와 Spring Boot 애플리케이션이 함께 컨테이너로 기동됩니다.
2. IntelliJ에서 직접 애플리케이션을 실행하고 싶다면 `docker-compose.yml`에서 `app` 서비스를 주석 처리한 뒤 IntelliJ에서 Spring Boot를 구동하세요.

---

# 컨벤션

## 1. 네이밍 규칙

### 명명 규칙

- 클래스: PascalCase (예: `UserProfile`, `ProductManager`)
- 메서드, 폴더명, 변수: camelCase (예: `getUserData`, `userInfo`)
- 상수: UPPER_SNAKE_CASE (예: `MAX_RETRY_COUNT`)
- 파일 이름
  - Java: PascalCase (예: `UserSigninResponse.java`, `ProductManager.java`)
  - Python/Frontend: snake_case (예: `user_controller.js`, `app_config.ts`)
- DB: snake_case

### PK id 설정

- 엔티티 내에서는 `id`만 참조하여 코드 일관성을 유지합니다.
- Spring Boot + JPA 환경에서는 모든 엔티티에서 `private Long id`로 통일합니다.
- DB 컬럼은 `SpringPhysicalNamingStrategy` 덕분에 `product_id`, `order_id` 등으로 자동 매핑됩니다.
- N:1 관계에서는 FK가 항상 N쪽에 있으며 `@ManyToOne`만 있어도 매핑이 성립합니다. `@OneToMany`는 편의상 둘 뿐 필수는 아닙니다.

## 2. 폴더 구조

```
src
 └─ main
    └─ java
       └─ com.iherbyou
          └─ cart
             ├─ entity        ← 엔티티 클래스 (JPA @Entity)
             ├─ repository    ← JpaRepository 인터페이스
             ├─ service       ← 비즈니스 로직
             ├─ controller    ← 웹 계층 (API, MVC Controller)
             └─ dto           ← 데이터 전달용 DTO
          └─ community
             ├─ entity        ← 엔티티 클래스 (JPA @Entity)
             ├─ repository    ← JpaRepository 인터페이스
             ├─ service       ← 비즈니스 로직
             ├─ controller    ← 웹 계층 (API, MVC Controller)
             └─ dto           ← 데이터 전달용 DTO
```

- 도메인 단위 폴더를 생성하고 각 폴더 내부에 entity, repository, service, controller, dto를 둡니다.

## 3. 코드 스타일

- 코드 자동 정렬: macOS `⌥+⌘+L`, Windows `Ctrl+Alt+L`
- 사용하지 않는 import 삭제: macOS `⌘+X`, Windows `Ctrl+X`
- 메서드/필드 사이 한 줄 공백으로 가독성 확보
- Lombok 애노테이션 사용 통일: `@Getter`, `@Setter`, `@ToString`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`

### IntelliJ 단축키

| 기능 | macOS | Windows/Linux |
| --- | --- | --- |
| 코드 정렬 (Reformat Code) | ⌥+⌘+L | Ctrl+Alt+L |
| Introduce Variable | ⌥+⌘+V | Ctrl+Alt+V |
| Extract Method | ⌥+⌘+M | Ctrl+Alt+M |
| Generate (getter/setter 등) | ⌘+N | Alt+Insert |
| 라인 삭제 | ⌘+X | Ctrl+Y |
| 검색/바꾸기 | Ctrl+R | Ctrl+R |
| 라인 이동 | Shift+⌘+↑/↓ | Shift+Ctrl+↑/↓ |

### 추가 규칙

- Service는 다른 Service를 통해서만 호출하며 타 도메인의 Mapper를 직접 호출하지 않습니다.
- 각 클래스 상단에 간단한 설명 주석을 남깁니다.
- Boolean 변수명에는 `is` 접두사를 사용합니다 (예: `isExist`).
- 인터페이스 이름에는 `I` 접두사를 사용합니다 (예: `IPlayable`).

## 4. Git & 작업 플로우

### 브랜치 네이밍

```
main       → 운영 배포용
dev        → 개발 통합
feat-#     → 기능 단위
refactor-# → 리팩토링
fix-#      → 버그 수정
hotfix-#   → 긴급 수정
```

### PR 제목 예시

```
[Feat] 회원가입 API 추가
[Fix] 로그인 비밀번호 검증 오류 수정
[Refactor] JWT 토큰 검증 로직 분리
[Chore] logback 설정 변경
[Hotfix] 세션 만료 버그 수정
[Merge] 진행상황 공유
```

### 작업 순서

1. 이슈 생성 (템플릿 활용)
2. 브랜치 생성
3. 작업 진행
4. PR 작성 (dev ← 본인 브랜치)
5. 팀원 승인 대기

### Git 초기 세팅 예시

```bash
git remote remove origin
git remote add origin <본인 레포>
git remote add upstream <팀 레포>
git fetch upstream
git switch main
git switch dev
git fetch origin
git branch --set-upstream-to=upstream/dev dev
git branch --set-upstream-to=upstream/main main
```

### 작업 방법 (CLI)

```bash
git switch dev
git switch -c feat-<이슈번호>
# 작업 후
git commit -m '<커밋 메시지>'
git push --set-upstream origin feat-<이슈번호>
```

### 팀 레포 갱신

```bash
git pull
```

## 💡 중요

- "반박시 님 말이 맞음" 정신으로, 합의된 규칙을 우선시합니다.
- 변경하고 싶은 부분은 반드시 팀 합의 후 적용합니다.
- 오늘도 화이팅입니다! 😃
