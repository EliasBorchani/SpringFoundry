# Modular Monolith Architecture Specification (Spring Edition)

---

## 1️⃣ Goals

* Modular, maintainable backend with strong separation of concerns
* Independent deployable apps composed of modules
* Avoid god modules and tight coupling between features
* Enable future extraction into microservices
* Make Spring usage explicit and clear for developers unfamiliar with it

---

## 2️⃣ Key Principles

1. **Modules own the business logic**

    * Contain domain entities, value objects, repositories, and use cases
    * Do not depend on other modules
    * Do not contain REST controllers
    * Keep **Spring usage minimal**, mainly for repository adapters and configuration

2. **Apps are composition roots**

    * Each app is a **Spring Boot application** with its own `main()`
    * Owns all **REST controllers** and cross-module orchestration
    * Decides which modules are included

3. **Shared core modules**

    * Contain domain primitives (IDs, Money, Value Objects)
    * Infrastructure helpers (pagination, error DTOs, AccountContext)
    * Can be included in any module

4. **No module-to-module dependencies**

    * All cross-module communication happens in the **app layer**
    * Modules only depend on **core/shared modules**

---

## 3️⃣ Folder / Module Layout

```
root/
├── core/                        # Shared primitives / infrastructure
│   ├── domain/                  # IDs, Money, Value Objects
│   ├── auth/                    # AccountContext, Security filters
│   └── web/                     # ErrorResponse, Pagination DTOs
│
├── modules/                     # Feature modules (business logic)
│   ├── users/
│   │   ├── domain/              # @Entity, Value Objects, Domain Services
│   │   ├── application/         # @Service, Use Cases, Interfaces (ports)
│   │   └── infrastructure/      # @Repository, JPA adapters
│   ├── billing/
│   │   ├── domain/
│   │   ├── application/
│   │   └── infrastructure/
│   └── measurements/
│       ├── domain/
│       ├── application/
│       └── infrastructure/
│
└── apps/                        # Deployable applications
    ├── monolith-app/            # Combines all modules, has REST controllers
    ├── users-app/               # Users module only, REST controllers
    └── billing-app/             # Billing + measurements, REST controllers
```

---

## 4️⃣ Spring Concepts and Responsibilities

| Concept                      | Lives in              | Responsibility                                                                      |
| ---------------------------- | --------------------- | ----------------------------------------------------------------------------------- |
| `@SpringBootApplication`     | app                   | Entry point; starts Spring Boot, loads modules                                      |
| `@RestController`            | app                   | Exposes HTTP endpoints (routes)                                                     |
| `@Service`                   | module/application    | Encapsulates business use cases / domain orchestration inside a module              |
| `@Repository`                | module/infrastructure | Encapsulates DB access, JPA/Hibernate, queries                                      |
| `@Entity`                    | module/domain         | Maps database table to Kotlin objects                                               |
| DTOs (Data Transfer Objects) | module/application    | Transfer data between layers or to app controllers                                  |
| Interfaces / Ports           | module/application    | Expose module functionality to the app without revealing internal logic             |
| Filters / ArgumentResolvers  | core/auth             | Populate `AccountContext` (authenticated user, permissions, locale) for controllers |
| Shared primitives            | core/domain           | Common value objects (IDs, Money, enums) used across modules                        |
| Orchestration logic          | app                   | Calls multiple module services to produce combined results for routes               |

---

## 5️⃣ Rules

1. **Modules never reference other modules**
2. **Apps reference modules**
3. **Controllers only exist in apps**
4. **Modules expose functionality via interfaces (ports)**
5. **Cross-module calls only occur in apps** (or a dedicated orchestration layer in the app)
6. **Shared primitives are allowed everywhere**
7. **Repositories only access the module’s own entities**

---

## 6️⃣ Example: Cross-Module Route

**Scenario:** Fetch billing info + user preferences

* Modules: `users`, `billing`
* App: `monolith-app`

```kotlin
@RestController
class UserBillingController(
    private val userQuery: UserQueryService,      // from users module
    private val billingService: BillingService   // from billing module
) {
    @GetMapping("/users/{id}/billing-summary")
    fun summary(@PathVariable id: UUID): BillingWithPrefsDto {
        val user = userQuery.getUser(id)            // module logic
        val billing = billingService.getBilling(id) // module logic
        return BillingWithPrefsDto(user, billing)
    }
}
```

* **Key point:** `UserBillingController` lives in the app
* Modules (`users`, `billing`) do **not** know about each other

---

## 7️⃣ Benefits

* Clear separation of **business logic (modules)** and **transport/orchestration (apps)**
* Independent deployable apps composed of selected modules
* Modules are framework-agnostic → easier testing and extraction
* Future extraction to microservices is possible without rewriting logic

---

## 8️⃣ Optional Enhancements

* OpenAPI-first for API contracts and DTO generation
* ArchUnit to enforce module boundaries
* Versioned modules for microservice extraction
