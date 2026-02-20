# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Stack

- **Language:** Kotlin 2.2.21 on Java 21
- **Framework:** Spring Boot 4.0.0 + Spring Data JPA + Flyway
- **Build:** Gradle 9.2.1 multi-project (Kotlin DSL) — `build-logic` included build + `gradle/libs.versions.toml` version catalog
- **Database:** PostgreSQL — profile `local` (Docker) or `prod` (env vars). No H2.
- **Auth:** JJWT 0.12.6 (HS256 access tokens) + BCrypt via `spring-security-crypto`
- **Tests:** JUnit 5 + ArchUnit 1.3.0

## Commands

```bash
# Start local PostgreSQL (one-time)
docker run --name foundry-postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=appdb -p 5432:5432 -d postgres:16

# Run an app
./gradlew :apps:monolith-app:bootRun
./gradlew :apps:users-app:bootRun

# Run all tests (requires local PostgreSQL)
./gradlew test

# Run tests for one subproject
./gradlew :modules:users:test
./gradlew :apps:monolith-app:test

# Run a single test class
./gradlew :apps:monolith-app:test --tests "*.ArchitectureTest"
./gradlew :apps:monolith-app:test --tests "*.AuthControllerTest"

# Build a fat JAR
./gradlew :apps:monolith-app:bootJar

# Compile without running tests
./gradlew :apps:monolith-app:classes :apps:monolith-app:testClasses
```

## Project structure

```
root/
├── gradle/libs.versions.toml    # Version catalog — single source of truth for versioned deps
├── build-logic/                 # Convention plugins (included build)
│   └── src/main/kotlin/
│       ├── foundry.kotlin-library.gradle.kts   # Base: Kotlin + Spring BOM + JUnit 5 + kotlin-reflect
│       ├── foundry.spring-module.gradle.kts    # Adds: spring-boot-starter-data-jpa + kotlin-spring/jpa
│       └── foundry.spring-app.gradle.kts       # Adds: Spring Boot plugin + web + data-jpa
│
├── core/                        # Shared primitives — no business logic
│   ├── domain/                  # EntityId, Money
│   ├── auth/                    # AccountContext (authenticated user per request)
│   └── web/                     # ErrorResponse, PageResponse<T>
│
├── modules/                     # Business modules — no controllers
│   ├── users/                   # UserEntity, UserService/UserServiceImpl
│   ├── measurements/            # MeasurementEntity, MeasurementService/MeasurementServiceImpl
│   └── authentication/          # CredentialEntity + RefreshTokenEntity, AuthService/AuthServiceImpl (JWT)
│
├── apps/                        # Deployable Spring Boot applications
│   ├── monolith-app/            # All modules; port 8080; ArchUnit + AuthControllerTest
│   └── users-app/               # users module only; port 8081
│
└── doc/
    └── architecture.md          # Full architecture documentation
```

## Naming conventions

| Role | Name | Annotation | Location |
|---|---|---|---|
| Entity | `UserEntity` | `@Entity` | `domain/` |
| DTO | `User` | — | `application/dto/` |
| Service interface | `UserService` | — | `application/` |
| Service impl | `UserServiceImpl` | `@Service` | `application/` |
| Repository interface | `UserRepository` | — | `application/` |
| Repository impl | `JpaUserRepository` | `@Repository` | `infrastructure/` |
| Controller | `UserController` | `@RestController` | `apps/` only |

**Key rules:**
- Entities are always suffixed `Entity`; DTOs use the plain domain name with no suffix
- App controllers inject `XxxService` (interface) only — never `XxxServiceImpl`, `JpaXxxRepository`, or anything from `infrastructure/`

## Module internal layers

Every `modules/<name>/` uses the same three-layer structure:

```
domain/           XxxEntity (@Entity), enums — no Spring, no JPA queries
application/      XxxService (interface), XxxServiceImpl (@Service),
                  XxxRepository (interface), dto/Xxx (plain DTO)
infrastructure/   JpaXxxRepository (@Repository implementing XxxRepository)
```

## Dependency rules (enforced by Gradle + ArchUnit)

```
apps  →  modules  →  core/domain
apps  →  core/auth
apps  →  core/web
modules  ↛  modules   ← FORBIDDEN
```

## Configuration / profiles

`apps/monolith-app/src/main/resources/`:
- `application.yml` — base config: JPA (`ddl-auto: validate`), server port `${PORT:8080}`, JWT defaults
- `application-local.yml` — `localhost:5432/appdb`, credentials `postgres/secret`
- `application-prod.yml` — `${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}` (Railway)

`src/test/resources/application.properties` — sets `spring.profiles.active=local` so tests use local PostgreSQL.

## Authentication module

`modules/authentication` — standalone, no dependency on `modules/users`.

- **`AuthService`** (interface): `register(userId, email, rawPassword)`, `login(email, password)`, `refresh(token)`, `revoke(token)`
- **JWT**: HS256, `sub = userId`, `email` claim, TTL from `auth.jwt.access-token-ttl-seconds`
- **Refresh tokens**: opaque UUID, stored in `refresh_tokens` table, rotated on use
- **`AuthController`** in `apps/monolith-app` calls both `UserService` (create user) and `AuthService` (create credentials) — the only place these two modules meet

## Spring Boot 4.0 quirks

- `@EntityScan` is in `org.springframework.boot.persistence.autoconfigure` (moved from `autoconfigure.domain`)
- `@AutoConfigureMockMvc` does not exist — use `MockMvcBuilders.webAppContextSetup(wac).build()` in `@BeforeEach`
- `TestRestTemplate` does not exist — use `MockMvc` (mock env) or `WebTestClient` (reactive)
- `ObjectMapper` is `tools.jackson.databind.ObjectMapper` (Jackson 3.x, not `com.fasterxml`)
- `kotlin-reflect` must be declared explicitly — Spring Data JPA needs it for Kotlin entity constructor discovery
- `PasswordEncoder.encode()` returns `String?` under `-Xjsr305=strict` — use `!!`

## Build system notes

Convention plugins (in `build-logic`) use **string literals** for BOM-managed Spring starters (`spring-boot-starter-web`, etc.). This is intentional — a Kotlin 2.2.x IR codegen bug in included builds crashes when calling `Provider<@EnhancedNullability ...>` return types from type-safe catalog accessors directly inside `implementation()`. All explicitly-versioned deps and non-BOM deps use `libs.*` accessors.

The `libs.versions.springBoot` type-safe accessor does NOT resolve inside `build-logic` (separate known Gradle issue with included builds). Plugin versions are hardcoded in `build-logic/build.gradle.kts`.

## Adding a new module

1. `mkdir -p modules/<name>/{domain,application/dto,infrastructure}`
2. Create `modules/<name>/build.gradle.kts` with `id("foundry.spring-module")`
3. Add `:modules:<name>` to `settings.gradle.kts`
4. Create `domain/XxxEntity` (`@Entity`), `application/XxxService` (interface), `application/XxxServiceImpl` (`@Service`), `application/XxxRepository` (interface), `application/dto/Xxx` (plain DTO), `infrastructure/JpaXxxRepository` (`@Repository`) — follow `modules/users/` as a pattern
5. Add `implementation(project(":modules:<name>"))` to the relevant `apps/*/build.gradle.kts`
