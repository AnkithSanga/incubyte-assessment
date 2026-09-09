# AI Workflows, Prompts & Collaboration Log

This artifact documents the intentional use of Agentic AI tools throughout the conception, architecture design, and development lifecycle of the ACME Salary Management Platform, following Incubyte's craftsmanship evaluation criteria.

---

## 1. Principles of AI Collaboration
- **Human-in-the-loop & Engineering Judgment:** AI is utilized as a force multiplier for ideation, design exploration, boilerplate reduction, and test generation, but architecture, design trade-offs, and verification remain strictly human-led.
- **Requirement Clarification First:** Before writing code, AI was prompted to critically analyze the problem statement, identify ambiguities, and formulate architectural questions to clarify requirements with stakeholders.
- **Incremental & Test-Driven:** AI generation is constrained to small, test-backed iterations to prevent hallucinations and maintain tight feedback loops.

---

## 2. Prompt History & Evolution

### Stage 1: Assessment Analysis & Clarification Formulation
- **Objective:** Analyze Incubyte take-home prompt, identify unstated assumptions, and formulate strategic questions for HR and Engineering stakeholders.
- **Key Outcome:** Identified key ambiguities around conversational AI vs. analytical dashboard, Excel drag-and-drop expectations, multi-currency normalization, and deployment targets.

### Stage 2: Product Requirements & Deliberate Exclusions
- **Objective:** Incorporate official stakeholder feedback from Sandli Srivastava into a formal one-page PRD.
- **Key Outcome:** Formulated [`docs/prd-requirements.md`](prd-requirements.md) setting strict scope boundaries (Base Salary focus, pre-authenticated HR persona, 10k seed script, seeded FX table).

### Stage 3: Architecture & Technology Selection
- **Objective:** Evaluate backend and frontend options honoring the Software Craftsperson (Java / Angular) JD while accommodating local environment constraints (no local Docker).
- **Key Outcome:** Designed Dual-Profile Database architecture: embedded H2 (PostgreSQL-compatibility mode) for zero-friction local development, paired with containerized PostgreSQL for evaluators. Documented in [`docs/architecture-adr.md`](architecture-adr.md).

### Stage 4: Backend Implementation & Performance Engineering (Phase 2)
- **Objective:** Implement Clean Architecture layers (Domain, Repository, Service, Web) with TDD.
- **Key Challenges & AI Interventions:**
  1. *High-Throughput Seeding:* Designed JPA batch seeding (`saveAll` in chunks of 1,000 with `Random(42)`) to populate 10,000 realistic international employee records in **912 milliseconds**.
  2. *Windows Encoding Fix:* Automated stripping of Windows PowerShell UTF-8 BOM (`\ufeff`) across all Java source files to resolve compiler incompatibilities.
  3. *Spring Boot 3.3 Page Serialization:* Configured `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` to adhere to modern Spring Data pagination standards.
  4. *Deterministic Test Suite:* Implemented 13 fast unit and slice tests (`@WebMvcTest`, Mockito, AssertJ) executing in under 9 seconds.
