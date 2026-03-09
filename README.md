# Zenith — Expense Tracker Backend

> A modern, enterprise-grade expense tracking REST API built with Spring Boot 4, following Clean Architecture and CQRS principles.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Running the Application](#running-the-application)
- [Configuration](#configuration)
- [API](#api)
  - [Authentication](#authentication)
  - [Endpoints](#endpoints)
  - [HAL Response Format](#hal-response-format)
- [Database Migrations](#database-migrations)
- [Code Generation](#code-generation)
- [Development Guidelines](#development-guidelines)

---

## Overview

**Zenith** is a personal finance and expense tracking platform. This repository contains the backend service — a stateless REST API secured with **AWS Cognito** (OAuth 2.0 / JWT), following industry best practices for structure, security, and maintainability.

Key design goals:

- **Clean Architecture** — strict separation between Domain, Application, Infrastructure and Presentation layers
- **CQRS** — Commands and Queries are handled by dedicated, single-responsibility handlers
- **API-first** — the REST contract is defined in `openapi/zenith-api.yml` and code is generated from it
- **HATEOAS / HAL** — all responses include hypermedia links for discoverability
- **Zero JPA leakage** — the Domain layer has no dependency on persistence frameworks

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Presentation Layer                   │
│  Controllers · RepresentationModel Assemblers            │
│  (implements generated OpenAPI interfaces)               │
└────────────────────────┬────────────────────────────────┘
                         │ Commands / Queries
┌────────────────────────▼────────────────────────────────┐
│                     Application Layer                    │
│  CommandHandler<C,R> · QueryHandler<Q,R>                 │
│  Application DTOs                                        │
└────────────────────────┬────────────────────────────────┘
                         │ Domain Repository Interfaces
┌────────────────────────▼────────────────────────────────┐
│                       Domain Layer                       │
│  Aggregates · Value Objects · Domain Exceptions          │
│  Repository Interfaces (ports)                           │
└─────────────────────────────────────────────────────────┘
                         ▲ Adapters implement ports
┌─────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                   │
│  JPA Entities · Spring Data Repositories                 │
│  Repository Adapters · MapStruct Mappers                 │
│  Security (Cognito JWT) · Liquibase Migrations           │
└─────────────────────────────────────────────────────────┘
```

### CQRS Flow

```
HTTP Request
    │
    ▼
Controller
    │── CreateExpenseCommand ──▶ CreateExpenseCommandHandler ──▶ ExpenseRepository
    │── GetExpensesByUserQuery ▶ GetExpensesByUserQueryHandler ─▶ ExpenseRepository
    │
    ▼
EntityModel<ExpenseResponse>  (HAL JSON)
```

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security · OAuth2 Resource Server · AWS Cognito |
| Persistence | Spring Data JPA · PostgreSQL · Hibernate |
| Migrations | Liquibase |
| Mapping | MapStruct 1.6.3 |
| API Contract | OpenAPI 3.0 · openapi-generator-maven-plugin 7.12.0 |
| Hypermedia | Spring HATEOAS (HAL format) |
| Documentation | SpringDoc OpenAPI UI 3.0.2 |
| Build | Maven (Maven Wrapper) |
| Code Generation | Lombok |

---

## Project Structure

```
src/main/java/.../zenith_backend/
│
├── domain/                         # Pure business logic — no framework dependencies
│   ├── model/                      # Aggregates & Value Objects
│   │   ├── UserAccount.java
│   │   ├── Expense.java
│   │   ├── Category.java
│   │   ├── Money.java              # Value Object (record)
│   │   ├── ExpenseStatus.java
│   │   └── CategoryType.java
│   ├── repository/                 # Repository interfaces (ports)
│   │   ├── UserAccountRepository.java
│   │   ├── ExpenseRepository.java
│   │   └── CategoryRepository.java
│   └── exception/                  # Domain exceptions
│
├── application/                    # Orchestration — no HTTP, no JPA
│   ├── command/                    # Write-side: Commands + CommandHandler<C,R>
│   ├── query/                      # Read-side: Queries + QueryHandler<Q,R>
│   ├── dto/                        # Internal transfer objects
│   └── handler/                    # One handler per command / query
│
├── infrastructure/                 # Framework & I/O concerns
│   ├── persistence/
│   │   ├── entity/                 # JPA entities (isolated from domain)
│   │   ├── repository/             # Spring Data JPA repositories
│   │   └── adapter/                # Implement domain repository interfaces
│   ├── mapper/                     # MapStruct mappers (domain ↔ JPA entity)
│   ├── security/                   # SecurityConfig · CognitoJwtConverter · UserSyncFilter
│   └── config/                     # HateoasConfig
│
└── presentation/                   # HTTP layer
    ├── AccountController.java
    ├── CategoryController.java
    ├── ExpenseController.java
    ├── GlobalExceptionHandler.java  # RFC 7807 Problem Details
    └── assembler/                   # RepresentationModelAssemblers (HAL links)

src/main/resources/
├── openapi/
│   └── zenith-api.yml              # Single source of truth for the API contract
├── db/changelog/
│   ├── db.changelog-master.yml
│   └── changes/
│       └── 001-init-schema.yml
└── application.properties
```

---

## Getting Started

### Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | via `./mvnw` (no local install needed) |
| Docker | any recent version |
| AWS Account | Cognito User Pool configured |

### Local Setup

**1. Start PostgreSQL via Docker**

```bash
docker run -d \
  --name zenith-postgres \
  -e POSTGRES_USER=zenith \
  -e POSTGRES_PASSWORD=zenith \
  -e POSTGRES_DB=zenith \
  -p 5432:5432 \
  postgres:16-alpine
```

**2. Configure AWS Cognito**

Set your Cognito User Pool values in `application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.<region>.amazonaws.com/<user-pool-id>
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://cognito-idp.<region>.amazonaws.com/<user-pool-id>/.well-known/jwks.json
```

Or provide them as environment variables (recommended for local dev):

```bash
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://cognito-idp.eu-central-1.amazonaws.com/<pool-id>
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://cognito-idp.eu-central-1.amazonaws.com/<pool-id>/.well-known/jwks.json
```

### Running the Application

```bash
# Generate OpenAPI sources + compile + run
./mvnw spring-boot:run
```

The application starts on **`http://localhost:8080`**.

Liquibase runs all pending migrations automatically on startup.

---

## Configuration

All configuration lives in `src/main/resources/application.properties`.  
Sensitive values should **never** be committed — use environment variables or a secrets manager in production.

| Property | Description | Default |
|---|---|---|
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/zenith` |
| `spring.datasource.username` | DB username | `zenith` |
| `spring.datasource.password` | DB password | `zenith` |
| `spring.jpa.hibernate.ddl-auto` | Schema management (always `validate` — Liquibase owns the schema) | `validate` |
| `spring.liquibase.change-log` | Path to Liquibase master changelog | `classpath:db/changelog/db.changelog-master.yml` |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | Cognito issuer URI | — |
| `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` | Cognito JWKS endpoint | — |
| `springdoc.swagger-ui.enabled` | Enable Swagger UI | `true` |

---

## API

### Authentication

All endpoints require a valid **Bearer JWT** issued by AWS Cognito.

```
Authorization: Bearer <cognito-id-token>
```

On the first authenticated request, the backend automatically provisions a `UserAccount` for the caller based on the JWT `sub` claim (via `CognitoUserSyncFilter`).

Public endpoints (no auth required):

| Path | Description |
|---|---|
| `GET /v3/api-docs` | OpenAPI JSON spec |
| `GET /swagger-ui.html` | Swagger UI |

### Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/account/me` | Get the current user's profile |
| `GET` | `/api/v1/expenses` | List expenses (paginated, filterable) |
| `GET` | `/api/v1/expenses/{id}` | Get a single expense |
| `POST` | `/api/v1/expenses` | Create an expense |
| `PUT` | `/api/v1/expenses/{id}` | Update an expense |
| `DELETE` | `/api/v1/expenses/{id}` | Soft-delete an expense |
| `GET` | `/api/v1/categories` | List all categories |
| `POST` | `/api/v1/categories` | Create a category |

**Query parameters for `GET /api/v1/expenses`:**

| Parameter | Type | Description |
|---|---|---|
| `page` | `integer` | Page number (default: `0`) |
| `size` | `integer` | Page size (default: `20`) |
| `categoryId` | `uuid` | Filter by category |
| `from` | `date` | Filter from date (ISO 8601) |
| `to` | `date` | Filter to date (ISO 8601) |

### HAL Response Format

All responses are served as `application/hal+json`. Each resource includes a `_links` object for hypermedia navigation.

**Single Expense (`GET /api/v1/expenses/{id}`):**

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "categoryId": "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed",
  "categoryName": "Groceries",
  "amount": 42.50,
  "currency": "EUR",
  "description": "Weekly shopping",
  "date": "2026-03-09",
  "_links": {
    "self":     { "href": "http://localhost:8080/api/v1/expenses/3fa85f64-..." },
    "expenses": { "href": "http://localhost:8080/api/v1/expenses?page=0&size=20" },
    "update":   { "href": "http://localhost:8080/api/v1/expenses/3fa85f64-..." },
    "delete":   { "href": "http://localhost:8080/api/v1/expenses/3fa85f64-..." }
  }
}
```

**Paged Expense List (`GET /api/v1/expenses`):**

```json
{
  "_embedded": {
    "expenseResponseList": [ { "...": "..." } ]
  },
  "_links": {
    "self":  { "href": "http://localhost:8080/api/v1/expenses?page=0&size=20" },
    "first": { "href": "http://localhost:8080/api/v1/expenses?page=0&size=20" },
    "next":  { "href": "http://localhost:8080/api/v1/expenses?page=1&size=20" },
    "last":  { "href": "http://localhost:8080/api/v1/expenses?page=4&size=20" }
  },
  "page": {
    "size": 20,
    "totalElements": 87,
    "totalPages": 5,
    "number": 0
  }
}
```

---

## Database Migrations

Schema changes are managed by **Liquibase**. All changesets live in:

```
src/main/resources/db/changelog/
├── db.changelog-master.yml        # Master include file
└── changes/
    └── 001-init-schema.yml        # Initial schema (user_accounts, categories, expenses)
```

**Rules:**
- Never modify an existing changeset — always add a new one
- Name new files with an incrementing prefix: `002-add-tags.yml`, `003-...`
- Use YAML format for consistency

---

## Code Generation

The API contract is defined once in **`src/main/resources/openapi/zenith-api.yml`** and is the single source of truth.

During `mvn generate-sources`, the `openapi-generator-maven-plugin` generates:

| Generated artifact | Package | Description |
|---|---|---|
| `AccountApi.java` | `presentation.api` | Interface with `getMe()` |
| `ExpensesApi.java` | `presentation.api` | Interface with all expense operations |
| `CategoriesApi.java` | `presentation.api` | Interface with category operations |
| `ExpenseResponse.java` etc. | `presentation.model` | Request / response DTOs |

> **Important:** Never edit files under `target/generated-sources/` — they are regenerated on every build.  
> To change the API, edit `zenith-api.yml` and rebuild.

---

## Development Guidelines

### Adding a new feature

1. **Define the contract** — add/update the endpoint in `zenith-api.yml`
2. **Domain first** — add any new domain model, value object or repository interface
3. **Application layer** — create a `Command`/`Query` record + a `CommandHandler`/`QueryHandler`
4. **Infrastructure** — add the JPA entity, Spring Data method, repository adapter and MapStruct mapping if needed
5. **Presentation** — implement the controller method, wire through the handler, add links in the assembler

### Layer dependency rules

```
Presentation  →  Application  →  Domain  ←  Infrastructure
```

- **Domain** must never import from Application, Infrastructure or Presentation
- **Application** must never import JPA annotations or Spring Data
- **Infrastructure** may import Domain and Application
- **Presentation** may only import Application DTOs and its own model package

### Error handling

All errors are returned as **RFC 7807 Problem Details** (`application/problem+json`):

```json
{
  "type": "/errors/expense-not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Expense not found: 3fa85f64-..."
}
```

Handled in `GlobalExceptionHandler` — add new `@ExceptionHandler` methods there for new domain exceptions.

