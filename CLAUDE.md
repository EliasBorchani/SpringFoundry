# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

- **Language:** Kotlin 2.2.21 on Java 21
- **Framework:** Spring Boot 4.0.0 + Spring Data JPA
- **Build:** Gradle 9.2.1 multi-project (Kotlin DSL) with `build-logic` included build + `gradle/libs.versions.toml` version catalog
- **Database:** H2 in-memory (dev); swap datasource config per-app for production
- **Test runner:** JUnit 5 + ArchUnit 1.3.0 (architecture enforcement)

## Commands

```bash
# Build everything
./gradlew build

# Run a specific app
./gradlew :apps:monolith-app:bootRun
./gradlew :apps:users-app:bootRun
./gradlew :apps:billing-app:bootRun

# Run all tests
./gradlew test

# Run tests for one subproject
./gradlew :modules:users:test
./gradlew :apps:monolith-app:test

# Run a single test class
./gradlew :apps:monolith-app:test --tests "*.ArchitectureTest"

# Build a fat JAR for an app
./gradlew :apps:monolith-app:bootJar
```

## Project structure

```
root/
├── gradle/
│   └── libs.versions.toml       # Version catalog — single source of truth for all deps
├── build-logic/                 # Convention plugins (included build, replaces old buildSrc)
│   └── src/main/kotlin/
│       ├── foundry.kotlin-library.gradle.kts   # Base: Kotlin + Spring BOM via platform() + JUnit 5
│       ├── foundry.spring-module.gradle.kts    # Adds: Spring Data JPA + kotlin-spring/jpa
│       └── foundry.spring-app.gradle.kts       # Adds: Spring Boot plugin + web + H2
│
├── core/                        # Shared primitives — allowed everywhere, no business logic
│   ├── domain/                  # EntityId (base for typed IDs), Money value object
│   ├── auth/                    # AccountContext (authenticated user per request)
│   └── web/                     # ErrorResponse, PageResponse<T>
│
├── modules/                     # Feature modules — pure business logic, no controllers
│   ├── users/
│   ├── billing/
│   └── measurements/
│
└── apps/                        # Deployable Spring Boot applications
    ├── monolith-app/            # All modules; port 8080; has ArchUnit tests
    ├── users-app/               # users module only; port 8081
    └── billing-app/             # billing + measurements modules; port 8082
```

## Architecture

### Module internal layers

Each `modules/<name>/` follows the same three-layer structure:

```
domain/           @Entity classes, enums — no Spring, no JPA queries
application/      @Service, port interfaces, DTOs — framework-agnostic business logic
  dto/            Data classes transferred between layers and to app controllers
infrastructure/   Spring Data JpaRepository interfaces + @Repository adapters
```

### Two kinds of ports

Every module exposes two interface types, both in `application/`:

| Interface | Direction | Example | Implemented by |
|---|---|---|---|
| `XxxPort` | Inbound — what apps call | `UserPort` | `UserService` (`@Service`) |
| `XxxRepository` | Outbound — what the service needs | `UserRepository` | `XxxRepositoryAdapter` (`@Repository`) |

Apps inject only the `XxxPort` interface; they never import `XxxService` or infrastructure types.

### Dependency graph

```
apps  →  modules  →  core/domain
apps  →  core/auth
apps  →  core/web
modules  →  core/domain
```

Modules never depend on other modules. This is **enforced at the Gradle level** — a module's `build.gradle.kts` simply does not declare another module as a dependency.

### Cross-module orchestration (app layer only)

```kotlin
// apps/monolith-app — the ONLY place two modules may interact
@RestController
class UserBillingController(
    private val userPort: UserPort,      // from modules:users
    private val billingPort: BillingPort // from modules:billing
) { ... }
```

### Spring Boot app configuration

All three apps use the same base annotations to scan across module packages:
```kotlin
@SpringBootApplication(scanBasePackages = ["io.github.eliasborchani.foundry"])
@EntityScan("io.github.eliasborchani.foundry")           // spring-boot-persistence (Spring Boot 4+)
@EnableJpaRepositories("io.github.eliasborchani.foundry") // spring-data-jpa
```
> Note: In Spring Boot 4.0, `@EntityScan` moved from `org.springframework.boot.autoconfigure.domain`
> to `org.springframework.boot.persistence.autoconfigure`.

### ArchUnit (enforced in monolith-app tests)

`apps/monolith-app/src/test/.../ArchitectureTest.kt` contains three rules:
1. Modules must not depend on each other (`slices().notDependOnEachOther()`)
2. `@RestController` only allowed in `apps` packages
3. Domain classes must not import Spring

## Adding a new module

1. Create `modules/<name>/build.gradle.kts` with `id("foundry.spring-module")`
2. Add `:modules:<name>` to `settings.gradle.kts`
3. Follow the `domain / application / infrastructure` structure
4. Expose functionality only via a `XxxPort` interface in `application/`
5. Add the module as a dependency in the relevant `apps/*/build.gradle.kts`
