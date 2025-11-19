# Repository Guidelines

## Project Structure & Module Organization
This Gradle-based Spring Boot 3.5 service keeps core code in `src/main/java/com/final_team4/finalbe`. Application bootstrap lives in `FinalBeApplication`, and shared config such as `SwaggerConfig` sits under `_core/config`. HTTP assets or Thymeleaf views belong in `src/main/resources/static` and `templates`, while environment values should go in `application.properties`. Tests mirror production packages in `src/test/java` to keep package visibility aligned.

## Build, Test, and Development Commands
- `./gradlew bootRun` – launches the API locally on port 8080 with Swagger UI at `/swagger-ui/index.html`.
- `./gradlew build` – compiles with the Java 21 toolchain and produces `build/libs/*.jar`; runs unit tests.
- `./gradlew test` – executes the JUnit 5 suite without assembling artifacts; prefer before pushing.
- `./gradlew clean` – removes build outputs when switching branches or toolchains.
Run all commands from the repo root; `gradlew` bundles the required Gradle version.

## Coding Style & Naming Conventions
Follow standard Spring idioms: 2-space indentation, braces on new lines, and descriptive class names ending with `Controller`, `Service`, or `Config` (e.g., `SwaggerConfig`). Keep packages lowercase with underscores only where already established (`_core`). REST endpoints should use kebab-case paths (`/bookings/{id}`) and DTOs should be immutable where possible.

## Testing Guidelines
Use JUnit 5 (`spring-boot-starter-test`) and place test classes next to their targets with a `*Tests` suffix, as shown by `FinalFeApplicationTests`. Cover controller mappings, service logic, and configuration beans; include Swagger bean availability checks when touching `SwaggerConfig`. Aim for meaningful assertions instead of context-only tests. Run `./gradlew test` locally before creating pull requests to keep CI green.

## Commit & Pull Request Guidelines
Recent history uses lowercase type prefixes (`chore:`, `fix:`). Keep messages under 72 characters in the subject, followed by details in the body if needed. Each PR should link the tracking issue, describe endpoints or configs touched, and attach screenshots or Swagger snippets when changing API contracts. Include reproduction steps for bugs and highlight testing evidence (`./gradlew test` output or Postman collection).

## Swagger & Configuration Notes
Public API docs are generated via `SwaggerConfig`; expose new endpoints by annotating controllers and documenting request/response models. Sensitive credentials should be injected through environment-specific `application-*.properties` files rather than committing secrets.
