# Repository Guidelines

## Project Structure & Module Organization
- Gradle-based Spring Boot 3.5 service; core code in `src/main/java/com/final_team4/finalbe` with `FinalBeApplication` as the entry point.
- Shared configuration (e.g., `SwaggerConfig`) resides in `_core/config`; feature packages stay lowercase with existing underscores only where present.
- Web assets live in `src/main/resources/static` and `templates`; environment values belong in `src/main/resources/application*.properties` (do not commit secrets).
- Tests mirror production packages under `src/test/java` with matching package names; build outputs land in `build/libs`.

## Build, Test, and Development Commands
- `./gradlew bootRun` — run the API locally on port 8080; Swagger UI available at `/swagger-ui/index.html`.
- `./gradlew test` — execute the JUnit 5 suite without assembling artifacts; use before pushing.
- `./gradlew build` — compile with the Java 21 toolchain, run tests, and produce `build/libs/*.jar`.
- `./gradlew clean` — remove build outputs when switching branches or toolchains.

## Coding Style & Naming Conventions
- Use 2-space indentation and place braces on new lines; follow standard Spring idioms with constructor injection where possible.
- Class names end with their role (`*Controller`, `*Service`, `*Config`); REST endpoints use kebab-case paths like `/bookings/{id}`.
- Keep DTOs immutable when practical; avoid new package names unless they follow the existing lowercase pattern.

## Testing Guidelines
- Use JUnit 5 via `spring-boot-starter-test`; name test classes with a `*Tests` suffix alongside their targets.
- Cover controller mappings, service logic, and configuration beans (including Swagger bean availability); prefer meaningful assertions over context-only tests.
- Run `./gradlew test` locally before PRs; add focused tests when touching controllers or configs.

## Commit & Pull Request Guidelines
- Commit messages follow lowercase type prefixes (e.g., `chore:`, `fix:`) with subjects under 72 characters.
- PRs should link the tracking issue, describe endpoints/configs touched, and include Swagger snippets or screenshots when API contracts change.
- Provide reproduction steps for bug fixes and note testing evidence (e.g., `./gradlew test` output or Postman collection).

## Security & Configuration Tips
- Keep secrets out of version control; inject via `application-*.properties` or environment variables.
- Document new public endpoints and models so they appear in Swagger; ensure sensitive configs stay environment-specific.
