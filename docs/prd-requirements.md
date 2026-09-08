# Product Requirements Document (PRD) — Salary Management Platform

**Document Status:** Working Draft (Pre-Implementation Alignment)  
**Target Persona:** HR Manager (ACME Organization)  
**Author:** Ankith Sanga  

---

## 1. Objective & Strategic Goal
Transition ACME Organization from manual, fragmented, multi-regional Excel spreadsheets to an enterprise-grade, high-performance web-based Salary Management and Compensation Intelligence platform. The platform empowers HR leadership to manage 10,000+ employee records and extract immediate insights regarding compensation distribution across international offices.

---

## 2. In-Scope Features (MVP)

### 2.1 Core Employee Directory & Salary Management
- **High-Performance Grid:** Paginated, server-side filtered, and sorted directory supporting 10,000+ employees.
- **Multi-Country Support:** Capture and display salaries in local currencies (USD, EUR, INR, GBP, CAD, etc.) alongside normalized base conversion (USD) for corporate comparison.
- **Compensation Breakdown:** Model Base Salary, Variable / Bonus, Allowances, and Total Annual CTC.
- **CRUD Operations:** Ability for HR to view, add, update, and deactivate employee compensation packages.

### 2.2 Compensation Intelligence & Analytics
- **Pay Equity & Distribution Dashboards:** Interactive visual breakdown of median, average, min, and max compensation segmented by:
  - Department (e.g., Engineering, Sales, Product, Marketing, HR, Finance)
  - Geographic Region / Country
  - Seniority / Job Level Band (Junior, Mid, Senior, Lead, Executive)
- **Top / Bottom Quartile Insights:** Instant identification of salary percentiles and outliers.

### 2.3 Conversational AI Compensation Querying
- **Natural Language HR Querying:** An AI interface allowing HR to ask free-form analytical questions:
  - *"What is the median compensation for Senior Engineers in Germany compared to the US?"*
  - *"Which department has the highest variable pay ratio?"*
  - *"Show me employees whose base compensation is outside their band range."*
- **Pluggable Architecture:** Fallback mode with rule-based/SQL analytics generator ensuring the app functions completely offline without requiring external API keys.

### 2.4 Data Ingestion & Seeding
- **Drag-and-Drop Ingestion:** Support uploading Excel (.xlsx) or CSV files with real-time validation and error reports for invalid rows.
- **Deterministic 10,000 Employee Seeder:** Automated CLI / backend seeder populating 10,000 realistic international records to simulate real-world ACME scale out-of-the-box.

---

## 3. Deliberately Excluded Features (Out of Scope & Rationale)

| Excluded Feature | Reasoning & Justification |
| :--- | :--- |
| **Direct Payroll Processing & Bank Payout Integration** | Bank API integrations (ACH, SEPA, SWIFT) and disbursement processing are specialized payment gateway operations. The assessment focuses on salary management, reporting, and planning, not transaction processing. |
| **Complex Country-Specific Tax & Statutory Deductions Engine** | Tax jurisdictions (e.g., US 401k/W-2, German Solidaritätszuschlag, Indian TDS/PF) change frequently and require dedicated compliance microservices. For the MVP, we focus on Gross Total CTC and taxable base rather than localized net tax calculators. |
| **Employee Self-Service Portal** | The target persona is specifically the **HR Manager**. Expanding access to 10,000 general employees requires complex multi-tenant employee self-service UI and separate RBAC permissions, diluting focus from HR analytics. |
| **Full OAuth2 / Active Directory / SSO Integration** | Enterprise SSO (Okta, Azure AD) introduces external identity provider dependencies. A streamlined JWT / role-gated session tailored for HR evaluation provides clean security without setup overhead. |
| **Continuous Live Forex API Feeds** | Live real-time currency fluctuations introduce external network latency and rate-limiting risks during evaluation. A cached exchange rate table or configurable reference table provides deterministic and reliable analytics. |

---

## 4. Non-Functional Requirements (NFRs)
- **Performance:** Sub-200ms response time for paginated queries over 10,000 records; client-side rendering with zero UI lag.
- **Data Integrity:** Strict validation on salary amounts, currency codes, email uniqueness, and employee status.
- **Determinism:** Seed scripts and automated tests must produce repeatable, deterministic results across all environments.
- **Craftsmanship:** Test-Driven Development (TDD), comprehensive unit test coverage, clean code, and clear commit history.
