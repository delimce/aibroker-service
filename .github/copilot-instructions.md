<!-- Copilot instructions for the aibroker-service repository -->
# AIBroker service — AI coding agent guidance

This file gives concise, actionable guidance to AI coding agents (Copilot-style) working on this repository. Focus on discoverable patterns, build/test/run commands, and files that show project conventions.

1) Big picture (what this project is)
- A Spring Boot 3.x service (Java 17) that brokers requests to multiple LLM providers. Main entry: `src/main/java/com/delimce/aibroker/AibrokerApplication.java`.
- Package layout: `application` (use-case services), `infrastructure` (controllers/adapters), `domain` (entities, DTOs, ports, repositories), `config` (security, caching, flyway). Follow this separation: business logic in `application`, web layer in `infrastructure/controllers`, persistence in `domain/repositories`.

2) How to build, test, and run (exact commands)
- Build & package: `./mvnw clean package` — produces `target/aibroker-service-*.jar`.
- Run locally (reads `.env` via `dotenv-java`): copy `.env.example` -> `.env` then:
  - Dev run: `./start.sh` (wrapper that runs the jar with env loaded). The application also runs with `java -jar target/aibroker-service-*.jar`.
- Database (dev): uses docker-compose; start DB container with `docker-compose up -d db`.
- Flyway migrations: `./flyway.sh info` / `./flyway.sh migrate` / `./flyway.sh clean` — script sources `.env`.
- Tests:
  - Unit: `./mvnw test`.
  - Integration (with test profile): `./mvnw test -Dspring.profiles.active=test` (test config in `src/main/resources/application-test.yml`).

3) Key conventions and patterns (repository-specific)
- Security: JWT-based stateless security. See `config/SecurityConfig.java` and `config/JwtAuthenticationFilter.java`. Controllers that must be public include `/account/**`, `/health`, and swagger endpoints (see `WHITELISTED_URLS`).
- DTOs and validation: Controllers accept validated DTOs (Jakarta validation). See `infrastructure/controllers/llm/ChatRequestController.java` as an example — it uses `@Valid` on `ModelRequest`.
- Services return `domain.dto.ApiResponse` via controller helper `BaseController` which centralizes success/error response shape. Prefer using the existing `responseOk`, `illegalArgumentExceptionResponse`, and `unhandledExceptionResponse` helpers.
- Mapping: MapStruct is the default mapping tool. Annotation processor configured in `pom.xml` — use `@Mapper` with `componentModel = "spring"` (already enforced by compiler arg).
- Caching: `@EnableCaching` at main app and `config/CacheConfig.java` defines cache behavior. Use Spring caching annotations where appropriate.

4) Integration points & external dependencies
- LLM integrations live behind application services (`application/llm/*`) and adapter/controller layers in `infrastructure/adapters` or `infrastructure/controllers/llm`. Inspect these when adding new provider clients.
- Database: MySQL runtime dependency; Flyway migrations in `src/main/resources/db/migration` (read the migration README in that directory for conventions).
- Environment variables: loaded from a `.env` file at startup (see `AibrokerApplication` using `dotenv-java`). Prefer adding secrets to environment variables and not checked-in files.

5) Testing & CI signals an AI should follow
- Tests use JUnit (maven-surefire). Test class naming: `*Test.java` (configured in `pom.xml`). When editing code, run `./mvnw test` locally to ensure no regressions.
- Coverage: JaCoCo configured — excludes config classes and generated mappers (see `pom.xml`). Keep tests fast and focus on unit-testing `application` services with mocked repositories/ports.

6) Helpful code examples (copy/paste references)
- Load env and start app: `AibrokerApplication.java` (uses `Dotenv.configure().load()` then `SpringApplication.run(...)`).
- Security filter pattern (authenticate by JWT): `config/JwtAuthenticationFilter.java` — extract email from token via `domain.ports.JwtTokenInterface` and set a `UsernamePasswordAuthenticationToken` with empty authorities.
- Controller pattern: wrap service call in try/catch and use `BaseController` helper methods for consistent `ApiResponse`.

7) When changing DB schema
- Add Flyway SQL file to `src/main/resources/db/migration` using the existing naming convention. Update `README.md` under migrations and run `./flyway.sh migrate` during local testing.

8) What to avoid / project-specific gotchas
- Do not assume Spring will auto-load `.env`—the app explicitly reads it in `AibrokerApplication`. Tests that require env variables rely on `application-test.yml` or explicit test setup.
- Authentication objects set into SecurityContextHolder are User entities (not Spring UserDetails). Security code expects that in places.

9) Useful files to inspect when onboarding
- `pom.xml` — build, compiler processors, and test includes
- `src/main/java/com/delimce/aibroker/config` — security, jwt filter, flyway config
- `src/main/java/com/delimce/aibroker/application` — core service logic
- `src/main/java/com/delimce/aibroker/infrastructure/controllers` — REST endpoints and response patterns
- `src/main/resources/db/migration/README.md` — migration rules
- `start.sh`, `flyway.sh`, `docker-compose.yml`, `.env.example` — runtime scripts and env examples

10) If you need to run or debug locally
- To run with logs visible: `./mvnw spring-boot:run` or run the jar directly; confirm `.env` is present.
- Use `test-api.http` for quick API calls in VS Code (REST Client) — examples live at repo root.

If anything in this file is unclear or you want more detail (examples for a specific module or a CI workflow), tell me which area and I will expand or iterate.
