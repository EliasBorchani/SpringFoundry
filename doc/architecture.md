# Architecture

SpringFoundry is structured as a **modular monolith** that can be decomposed into independent deployable services without touching business logic.

---

## Core idea

```
┌─────────────────────────────────────────────────────────┐
│  apps/                  (HTTP layer, composition root)   │
│   └── monolith-app  ─── controllers, Spring Boot main   │
├─────────────────────────────────────────────────────────┤
│  modules/               (business logic, self-contained) │
│   ├── users                                             │
│   ├── measurements                                      │
│   └── authentication                                    │
├─────────────────────────────────────────────────────────┤
│  core/                  (shared primitives, no logic)    │
│   ├── domain   ── EntityId, Money                       │
│   ├── auth     ── AccountContext                        │
│   └── web      ── ErrorResponse, PageResponse           │
└─────────────────────────────────────────────────────────┘
```

**One rule above all:** modules never import other modules. All cross-module calls happen in the app layer. This is enforced by Gradle (modules simply don't declare each other as dependencies) and verified at test time by ArchUnit.

---

## Module internals — hexagonal layers

Each `modules/<name>/` follows the same three-layer structure:

```
modules/users/
├── domain/           Pure Kotlin — @Entity classes, enums, value objects
│                     No Spring annotations, no JPA queries, no imports from other modules
│
├── application/      Business logic layer
│   ├── UserPort      Inbound interface — what the app layer calls
│   ├── UserService   @Service implementing UserPort
│   ├── UserRepository  Outbound interface — what the service needs from persistence
│   └── dto/          Data classes passed between layers and to controllers
│
└── infrastructure/   Spring / JPA wiring
    ├── UserJpaRepository      extends JpaRepository<User, UUID>
    └── UserRepositoryAdapter  @Repository implementing UserRepository
```

### Two kinds of ports

| Interface | Direction | Implemented by | Called by |
|---|---|---|---|
| `XxxPort` | **Inbound** — public API of the module | `XxxService` (`@Service`) | App controllers |
| `XxxRepository` | **Outbound** — what the service needs | `XxxRepositoryAdapter` (`@Repository`) | `XxxService` |

App controllers only ever inject `XxxPort`. They never import `XxxService`, `XxxJpaRepository`, or anything from `infrastructure/`.

---

## Dependency graph

```
apps/monolith-app  →  modules/users           →  core/domain
                   →  modules/measurements     →  core/domain
                   →  modules/authentication   →  core/domain
                   →  core/auth
                   →  core/web

apps/users-app     →  modules/users           →  core/domain
                   →  core/auth
                   →  core/web

modules/*          →  core/domain             (only allowed cross-module dep)
modules/*          ↛  modules/*               (FORBIDDEN — enforced by Gradle + ArchUnit)
```

---

## App layer — composition root

An app is just a `@SpringBootApplication` that:
1. Declares module JARs as Gradle `implementation` dependencies
2. Owns all `@RestController` classes
3. Performs cross-module orchestration when a single endpoint needs data from multiple modules

```kotlin
// Only the app layer may combine two modules
@RestController
class UserBillingController(
    private val userPort: UserPort,      // injected from modules:users
    private val billingPort: BillingPort // injected from modules:billing
) {
    @GetMapping("/users/{id}/billing-summary")
    fun summary(@PathVariable id: UUID): UserBillingSummaryDto { ... }
}
```

The modules (`users`, `billing`) have no knowledge of each other. The controller in the app layer is the only place they meet.

---

## Multi-app deployment model

Because each module is a self-contained JAR, the same code can be assembled into different deployment shapes:

| App | Included modules | Use case |
|---|---|---|
| `monolith-app` | all | Simple deployment, one process |
| `users-app` | users | Independent users service |

To extract a module into its own service: create a new `apps/<name>-app/`, declare only that module as a dependency, and add controllers. No business logic changes.

---

## Authentication module

`modules/authentication` implements a stateless JWT + opaque refresh-token flow:

- **Access token** — HS256 JWT, 15-minute TTL, signed with a configurable secret. Claims: `sub = userId`, `email`.
- **Refresh token** — UUID stored in the `refresh_tokens` table, 7-day TTL, **rotated on every use** (old token revoked, new one issued).
- **Passwords** — BCrypt via `spring-security-crypto` (no full Spring Security stack).

### Flow

```
POST /auth/register  →  create User (users module) + Credential  →  return TokenPairDto
POST /auth/login     →  verify password  →  issue JWT + refresh token  →  return TokenPairDto
POST /auth/refresh   →  validate + rotate refresh token  →  issue new JWT  →  return TokenPairDto
POST /auth/logout    →  mark refresh token revoked  →  204
```

The `AuthController` in `apps/monolith-app` orchestrates `UserPort` (to create the user) and `AuthPort` (to manage credentials and tokens). The two modules don't know about each other.

---

## Database

PostgreSQL with Flyway migrations. Profile-based configuration:

| Profile | Datasource | How to activate |
|---|---|---|
| `local` | `localhost:5432/appdb` | `SPRING_PROFILES_ACTIVE=local` |
| `prod` | `$DB_URL` / `$DB_USER` / `$DB_PASSWORD` | `SPRING_PROFILES_ACTIVE=prod` |

`ddl-auto: validate` — Hibernate validates the schema against Flyway-managed migrations rather than modifying it. Add a new migration file in `src/main/resources/db/migration/` whenever the schema changes.

---

## Build system

```
build-logic/                    Included build — convention plugins
├── foundry.kotlin-library      Base: Kotlin JVM + Spring BOM + JUnit 5 + kotlin-reflect
├── foundry.spring-module       Adds: spring-boot-starter-data-jpa + kotlin-spring/jpa plugins
└── foundry.spring-app          Adds: spring-boot-starter-web + Spring Boot plugin

gradle/libs.versions.toml       Version catalog — single source of truth for all versioned deps
```

Convention plugins (in `build-logic`) use string literals for BOM-managed Spring deps — this is intentional. A Kotlin 2.2.x IR codegen bug prevents using type-safe version catalog accessors for `Provider<@EnhancedNullability ...>` return types inside included builds. All explicitly-versioned deps and app-level deps use `libs.*` accessors.

---

## ArchUnit rules (enforced in `monolith-app` tests)

Three rules verified on every `./gradlew test`:

1. **Module isolation** — `slices().matching("..modules.(*)..")` must not depend on each other
2. **Controllers in apps only** — `@RestController` must reside in `..apps..` packages
3. **Clean domain** — classes in `..modules.*.domain..` must not import Spring

---

## Adding a new module

```bash
# 1. Create the module
mkdir -p modules/<name>/{domain,application/dto,infrastructure}
```

```kotlin
// 2. modules/<name>/build.gradle.kts
plugins { id("foundry.spring-module") }
dependencies { implementation(project(":core:domain")) }
```

```kotlin
// 3. settings.gradle.kts — add to include(...)
":modules:<name>"
```

```kotlin
// 4. Create domain/@Entity, application/XxxPort, application/XxxService,
//    application/XxxRepository, infrastructure adapters
//    following the pattern in modules/users/
```

```kotlin
// 5. apps/<target>/build.gradle.kts — add dependency
implementation(project(":modules:<name>"))
```

The module is now available to any app that declares the dependency. No changes to other modules required.
