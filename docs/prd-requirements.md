# Product Requirements Document (PRD) — ACME Salary Management Platform

**Document Status:** Approved Scope (Aligned with Stakeholder Clarifications)  
**Target Persona:** HR Manager (ACME Organization)  
**Author:** Ankith Sanga  
**Engineering Assessment:** Incubyte — Software Craftsperson  

---

## 1. Objective & Strategic Goal
Transition ACME Organization from manual, fragmented, multi-regional Excel spreadsheets to an enterprise-grade, high-performance web-based Salary Management and Compensation Intelligence platform. The platform empowers HR leadership to manage 10,000+ employee records and extract immediate operational and strategic insights regarding compensation distribution across international offices.

---

## 2. In-Scope Features (MVP)

### 2.1 Core Employee Directory & Salary Management
- **High-Performance Paginated Directory:** Filterable, searchable, and sortable directory supporting 10,000+ employees with sub-second response times.
- **Multi-Country Local Currency Storage:** Store compensation in the employee's native local currency (USD, EUR, GBP, INR, CAD, JPY, etc.).
- **FX Exchange Rate Normalization:** Seeded reference exchange-rate table converting local compensation into USD for unified organizational analytics.
- **Strict Base Salary Focus:** Model Annual Base Salary as the single source of compensation truth.

### 2.2 Compensation Intelligence & Analytics Dashboards
- **Executive KPI Cards:** Real-time visibility into:
  - Total Global Headcount (10,000)
  - Total Annual Payroll in USD
  - Average & Median Annual Base Salary in USD
  - Highest & Lowest Compensated Departments and Regions
- **Departmental Analytics:** Headcount, total payroll, and average/median compensation segmented across departments (Engineering, Product, Sales, Marketing, HR, Finance, Operations).
- **Geographic / Country Analytics:** Country-level compensation distribution and local-to-USD conversion metrics.
- **Salary Band Distributions:** Quartile and bracket distributions to identify pay concentration and outliers.

### 2.3 Deterministic 10,000-Employee Data Seeding
- **Automated Synthetic Seeder:** High-throughput backend seeder generating 10,000 realistic international records with authentic demographic and salary distributions upon application startup.
- **Idempotent Execution:** Skips seeding if records already exist to ensure fast subsequent boots.

### 2.4 Optional Stretch: Natural Language Compensation Assistant
- **Offline Deterministic Query Engine:** HR query endpoint answering questions about org pay (e.g., *"What is the median salary in Engineering vs Sales?"*, *"Compare pay in Germany and India"*) without external API key dependencies.

---

## 3. Deliberately Excluded Features (Out of Scope & Architectural Rationale)

| Excluded Feature | Reasoning & Justification |
| :--- | :--- |
| **In-App Excel / CSV File Upload UI** | Confirmed by stakeholder. Generating 10,000 synthetic records via automated backend seeding completely fulfills testing and evaluation needs without adding unnecessary UI/file-parsing overhead. |
| **Variable Pay, Bonuses, Equity & Allowances** | Confirmed by stakeholder. The platform strictly targets **Annual Base Salary**. Complex multi-component compensation models dilute the core assessment objectives. |
| **Country-Specific Statutory Tax & Payroll Deductions** | Local tax rules (e.g. US 401k, German Solidaritätszuschlag, Indian TDS/PF) change frequently and require external compliance engines. Focus is on Gross Annual Base Salary. |
| **Authentication, Login Flows & Granular RBAC** | Confirmed by stakeholder. The system assumes an internal enterprise deployment with a single pre-authenticated HR Manager persona. RBAC is reserved for future production iterations. |
| **Live Forex / Continuous Stock Feeds** | External live feeds introduce network latency and evaluation failure risks. A seeded, cached FX rate table guarantees deterministic, fast, and repeatable calculations. |

---

## 4. Non-Functional Requirements (NFRs)
- **Performance:** Sub-100ms response time for paginated queries and aggregated metrics over 10,000 records.
- **Data Integrity:** Strict constraints on email uniqueness, positive salary amounts, and valid ISO currency codes.
- **Zero-Friction Local Execution:** Dual-profile persistence enabling instant local launch on embedded H2 (PostgreSQL mode) without mandatory local Docker.
- **Craftsmanship & TDD:** Deterministic unit and integration tests with high code coverage.
