# Architecture Decision Records (ADR)

## ADR 001: Technology Stack Selection (Java 21/23 + Spring Boot 3 & Angular 18)
- **Status:** Accepted
- **Context:** The role is *Software Craftsperson / Java / Angular - II*. The platform requires an enterprise-ready backend and responsive UI.
- **Decision:** Implement the backend in Java 23 / Spring Boot 3.3.x with Spring Data JPA and Maven; the frontend in modern Angular with standalone components.
- **Consequences:** Provides strong type safety, robust test tooling, and direct alignment with Incubyte's craftsmanship standards.

## ADR 002: Dual-Profile Database Strategy (Embedded H2 & Containerized PostgreSQL)
- **Status:** Accepted
- **Context:** The developer environment does not have Docker installed, but reviewers and CI environments may test using Dockerized PostgreSQL.
- **Decision:** 
  - `local` profile (default): Uses Spring Boot's embedded H2 database running in PostgreSQL compatibility mode (`jdbc:h2:file:./data/salarydb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`).
  - `prod` / `docker` profile: Uses PostgreSQL 16 via Docker Compose or cloud hosting (Render/Railway).
- **Consequences:** Developer can launch the full system locally in seconds with `mvn spring-boot:run` without installing Docker, while reviewers have ready Docker Compose support.

## ADR 003: Native Currency Storage with Reference FX Rate Normalization
- **Status:** Accepted
- **Context:** Employees are paid across 6+ countries in local currencies (USD, EUR, GBP, INR, JPY, CAD). Cross-border analysis requires unified currency normalization.
- **Decision:** Store `annualBaseSalaryLocal` in the native currency and compute/store `annualBaseSalaryUsd` using a seeded, deterministic `ExchangeRate` reference table.
- **Consequences:** Eliminates external forex API dependencies and network flakiness during evaluation, ensuring deterministic, ultra-fast queries.

## ADR 004: In-Memory / JPA Batch Seeding for 10,000 Employees
- **Status:** Accepted
- **Context:** 10,000 synthetic employee records must be generated to simulate ACME scale without manual data entry or slow file uploads.
- **Decision:** Implement a Spring `CommandLineRunner` that populates 10,000 employees using batch inserts (`saveAll` with configured batch size) if the repository is empty.
- **Consequences:** Seeding completes in under 2 seconds on application startup. Idempotent check ensures subsequent startups are near-instantaneous.

## ADR 005: Scope Exclusions (Pre-Authenticated Persona & Base Salary Focus)
- **Status:** Accepted
- **Context:** Stakeholder guidance explicitly marked authentication/RBAC and multi-component compensation (bonuses, equity, taxes) as out of scope.
- **Decision:** Assume a single internal HR Manager persona; model only Annual Base Salary.
- **Consequences:** Keeps codebase lean, maintainable, and focused on core salary management and analytical insights.
