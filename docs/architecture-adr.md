# Architecture Decision Records (ADR)

## ADR 001: Technology Stack Selection (Java 21 + Spring Boot 3 & Angular 18)
- **Status:** Accepted
- **Context:** The project requires building an end-to-end, fully functional salary management software for an organization with 10,000 employees. The JD specifies the role as *Software Craftsperson / Java / Angular - II*.
- **Decision:** Use Java 21+ with Spring Boot 3 for the backend and Angular 18+ for the frontend.
- **Consequences:** 
  - Aligns with the core technical expectations of the applied role.
  - Strong compile-time type safety across both frontend and backend.
  - Excellent ecosystem for unit testing, enterprise patterns, and long-term maintainability.

## ADR 002: Relational Persistence Strategy (SQLite/H2 & PostgreSQL)
- **Status:** Accepted
- **Context:** The system needs to support 10,000 employee records with fast aggregation queries and minimal setup friction during assessment evaluation.
- **Decision:** Support dual-mode database configuration:
  - Default: Embedded Relational Database (SQLite / H2 in file or memory mode) for instant, zero-dependency local execution.
  - Container/Production: PostgreSQL via Docker Compose for production deployments.
- **Consequences:** Evaluators can run the application with zero external DB prerequisites while retaining production-readiness.

## ADR 003: Conversational AI Integration & Offline Resilience
- **Status:** Accepted
- **Context:** The requirement states the HR manager should be able to "answer questions about how the org pays people."
- **Decision:** Implement a dual-mode conversational query service:
  - Mode A: Direct LLM-powered natural language translation via API (OpenAI / Gemini / Anthropic).
  - Mode B: Offline deterministic analytical query parser with pre-built parametric aggregations (e.g. median by department, pay gap by gender, regional comparison) so that evaluation never fails due to missing API keys or network latency.
- **Consequences:** Seamless evaluation experience with zero risk of broken demos.
