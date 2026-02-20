# SpringFoundry

A personal playground for experimenting with Spring Boot — built around a modular monolith architecture that can be split into independent services without rewriting business logic.

The goal is to keep the codebase as a living reference for modern Spring Boot patterns: hexagonal architecture, profile-based configuration, JWT authentication, Flyway migrations, and ArchUnit-enforced module boundaries.

---

## What's inside

| Layer | What it does |
|---|---|
| `core/` | Shared primitives: typed IDs, Money, pagination, web error shapes, auth context |
| `modules/` | Self-contained business domains — each owns its entities, services, and DB adapters |
| `apps/` | Deployable Spring Boot applications that assemble modules and own REST controllers |

---

## Architecture in a sentence

> Modules own the business logic. Apps own the HTTP layer and wire modules together. Modules never talk to each other directly.

See [`doc/architecture.md`](doc/architecture.md) for the full picture.

---

## Running locally

**1. Start PostgreSQL**

```bash
docker run --name foundry-postgres \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=appdb \
  -p 5432:5432 \
  -d postgres:16
```

**2. Run the monolith**

```bash
./gradlew :apps:monolith-app:bootRun
```

The app starts on `http://localhost:8080`. Flyway runs migrations automatically on startup.

---

## Auth endpoints (quick reference)

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"me@example.com","displayName":"Me","password":"secret"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"me@example.com","password":"secret"}'

# Refresh
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<token>"}'

# Logout
curl -X POST http://localhost:8080/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<token>"}'
```

---

## Useful Gradle commands

```bash
# Build everything
./gradlew build

# Run an app
./gradlew :apps:monolith-app:bootRun
./gradlew :apps:users-app:bootRun

# Run all tests (requires local PostgreSQL)
./gradlew test

# Run tests for one project
./gradlew :apps:monolith-app:test
./gradlew :modules:users:test

# Build a deployable JAR
./gradlew :apps:monolith-app:bootJar
```

---

## The multi-app idea

Every module is compiled into an independent JAR. An app is just a `@SpringBootApplication` that declares which module JARs it depends on. This means:

- **`monolith-app`** — one process, all features, easiest to operate
- **`users-app`** — only the `users` module; can be deployed independently

New feature? Add a module. Need to extract it later? Remove it from `monolith-app`, promote it into its own app. No business logic changes required.

---

## Stack

- Kotlin 2.2.21 · Java 21
- Spring Boot 4.0 · Spring Data JPA · Flyway
- PostgreSQL (local via Docker, production via Railway env vars)
- JJWT 0.12 · spring-security-crypto
- Gradle 9.2 · `build-logic` convention plugins · `libs.versions.toml`
- JUnit 5 · ArchUnit 1.3
