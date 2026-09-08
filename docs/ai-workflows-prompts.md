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
- **Objective:** Draft a one-page PRD delineating clear MVP boundaries and justifying deliberate exclusions (e.g. payroll disbursement, country-specific statutory tax deductions, employee self-service).
- **Key Outcome:** Formulated [`docs/prd-requirements.md`](prd-requirements.md) to set clear scope before development.

### Stage 3: Architecture & Technology Selection
- **Objective:** Evaluate backend and frontend options honoring the Software Craftsperson (Java / Angular) JD.
- **Key Outcome:** Selected Spring Boot 3 (Java 21+) + Angular 18 (Standalone components, Signals) with dual-mode SQLite/H2 and PostgreSQL persistence.
