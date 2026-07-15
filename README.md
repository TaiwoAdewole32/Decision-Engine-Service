# Project: Rule-Based Decision Engine 

## Overview: 
A fairness-aware loan decision engine built with Java and Spring Boot. This project evaluates loan applications  using configurable business rules, generates explainable approval decisions, and is being designed to support bias auditing for more transparent lending workflows. 

**KEY PRINCIPLE**: Demographic data (age, employment) is STORED for auditing ONLY. It NEVER influences rule evaluation.

## Key Features
- Submit loan application data for evaluation
- Evaluate applications using rule-based decision logic
- Support approval outcomes such as `APPROVE`, `REJECT`, and `REVIEW`
- Add score-based decision factors
- Generate human-readable reasons for decisions
- Track which rules matched during evaluation
- Support compound conditions such as `AND`, `OR`, and `NOT`
- Detect approval disparities by demographic group (bias auditing)

## Architecture

```
 Loan Application (with demographics for audit only)
       |
       v
   CaseData
       |
       v
  RuleEngine (RULES USE ONLY FINANCIAL FIELDS)
       |
       v
Rules + Conditions (income, credit score, employment status, age)
       |
       v
   Rule Actions
       |
       v
  DecisionResult
       |
       v
  Store in SQLite (decision + demographics)
       |
       v
  Python Audit Service (analyzes demographics for bias)
```

## Tech Stack
- **Java 21**
- **Spring Boot**
- **Maven**
- **SQLite**
- **Python 3** (audit service)
- **Flask** (audit API)

## Getting Started

### Backend
```bash
cd ruleengine
mvn spring-boot:run
```

### Audit Service
```bash
cd audit
pip install -r requirements.txt
python app.py
```

## System Design & Architecture

### Core Concept: Architectural Fairness Enforcement

This system prevents algorithmic bias by **design**, not afterthought:

1. **Decision Engine (Java/Spring)** - Evaluates loans using ONLY financial metrics
   - Cannot access demographic data during rule evaluation
   - Field enum restricted to: `INCOME`, `CREDIT_SCORE`, `DEBT_TO_INCOME`, `HAS_LATE_PAYMENTS`, `REQUESTED_AMOUNT`
   - Demographics (age, employment) physically separated from rule logic

2. **Decision Storage (SQLite)** - Persists decisions WITH demographics for audit only
   - DecisionRecord stores: decision, score, reasons, matched rules, trace
   - Demographics stored AFTER decision made (audit trail only)
   - Immutable append-only design (no updates/deletes)

3. **Audit Service (Python/Flask)** - Analyzes for bias post-hoc
   - Reads decision database
   - Groups by demographics (age, income, employment)
   - Calculates approval rates per group
   - Detects disparities (if >10% difference → alerts)

### Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│              Loan Application Request                        │
│  (applicantID, age, income, credit_score, debt_to_income)   │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
            ┌────────────────────────────┐
            │  ApplicationController     │
            │  - Validates input         │
            │  - Strips demographics     │
            │  - Calls RuleEngine        │
            └────────────────────────────┘
                         │
                         ▼
            ┌────────────────────────────┐
            │  RuleEngine (Core Logic)   │
            │  - Only sees financial     │
            │    metrics (NOT age)       │
            │  - Evaluates 5 rules       │
            │  - Returns decision+score  │
            └────────────────────────────┘
                         │
                         ▼
            ┌────────────────────────────┐
            │  DecisionContext           │
            │  - Tracks: decision,       │
            │    score, reasons, rules   │
            └────────────────────────────┘
                         │
                         ▼
            ┌────────────────────────────┐
            │  DecisionRecord Repository │
            │  - Saves to SQLite         │
            │  - Stores demographics     │
            │    (for audit only)        │
            └────────────────────────────┘
                         │
        ┌────────────────┴────────────────┐
        │                                 │
        ▼                                 ▼
   JSON Response              SQLite Database
  (Decision + Reasons)       (decisions.db)
                                  │
                                  ▼
                    ┌──────────────────────────┐
                    │   BiasAudit (Python)     │
                    │   - Reads decisions.db   │
                    │   - Groups by age,       │
                    │     income, employment   │
                    │   - Detects disparities  │
                    └──────────────────────────┘
                                  │
                                  ▼
                         Audit Report
                    (Alerts if unfair pattern)
```

### Five Rules Evaluated (rules.json)

Rules are executed in priority order:

| Priority | Rule ID | Condition | Decision | Purpose |
|----------|---------|-----------|----------|---------|
| 1 | income-check | Income < $30k | REJECT | Hard financial floor |
| 2 | credit-score-check | Credit score < 620 | REJECT | Hard credit floor |
| 3 | late-payments-check | Has late payment history | REVIEW | Manual review flag |
| 4 | debt-to-income-check | Debt ratio > 43% | REVIEW | Manual review flag |
| 100 | approve-qualified | Income ≥$50k AND Credit ≥700 AND Debt ≤36% | APPROVE | Strong qualifiers |

**Key:** Rules 1-2 are "hard stops" (reject). Rules 3-4 flag for review. Rule 100 auto-approves if all metrics strong.

### Data Model

**CaseData (Input):**
```java
applicantID: String          // Unique ID
age: Integer                 // Stored for audit only
income: Integer              // Used in rules
creditScore: Integer         // Used in rules (300-850)
debtToIncome: Double         // Used in rules (0.0-1.0)
hasLatePayments: Boolean     // Used in rules
requestedAmount: Integer     // Used in rules
employmentStatus: String     // Stored for audit only
```

**DecisionRecord (Output - Persisted):**
```java
applicantID: String                      // Reference to applicant
decision: APPROVE | REJECT | REVIEW      // Final decision
score: Integer (0-200)                   // Encourages approval when qualified
reasons: List<String>                    // Why decision made
matchedRulesIDs: List<String>           // Which rules triggered
trace: List<TraceEntry>                 // Full evaluation trace
age: Integer                             // Stored for audit analysis
income: Integer                          // Stored for audit analysis
creditScore: Integer                     // Stored for audit analysis
employmentStatus: String                 // Stored for audit analysis
timestamp: LocalDateTime                 // When decision made
```

### Bias Prevention Mechanism

**How Demographics Are Blocked From Rules:**

1. ComparisonCondition.Field enum ONLY includes:
   - `INCOME`, `CREDIT_SCORE`, `DEBT_TO_INCOME`, `HAS_LATE_PAYMENTS`, `REQUESTED_AMOUNT`
   - Does NOT include: `AGE`, `GENDER`, `RACE`, `MARITAL_STATUS`

2. At compile time: If someone tries to add an age-based rule, they must:
   - Add `AGE` to Field enum
   - Violates code review (someone would catch it)
   - Can be prevented with static analysis

3. At runtime: RuleEngine only receives financial fields
   - Demographics passed to DecisionRecord AFTER decision
   - Audit service never influences decisions

**Proof of Fairness:**
- Same applicant with same financial metrics = same decision (regardless of age/employment)
- Stored in BiasPreventionTest.java (automated checks)

---

## APIs

### Java Backend (Port 8080)
- `POST /api/applications/evaluate` - Submit loan application
- `GET /api/applications/{applicantID}` - Get application history

### Python Audit (Port 5000)
- `GET /api/audit/income` - Approval rates by income bracket
- `GET /api/audit/age` - Approval rates by age group
- `GET /api/audit/credit-score` - Approval rates by credit score
- `GET /api/audit/employment` - Approval rates by employment status
- `GET /api/audit/disparities` - Flags significant approval differences
- `GET /api/audit/report` - Full audit report

---

## Testing Guide

### Step 1: Start the Java Backend

```bash
cd ruleengine
./mvnw spring-boot:run
```

**Expected output:**
```
Started RuleengineApplication in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8080
```

### Step 2: Verify Backend is Running

Open a new terminal and test the health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

**Expected response:**
```json
{"status":"UP"}
```

### Step 3: Test Rule Evaluation - APPROVE Case

Submit an applicant with strong finances (should auto-approve):

```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-001",
    "age": 35,
    "income": 65000,
    "creditScore": 750,
    "debtToIncome": 0.28,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected response:**
```json
{
  "decision": "APPROVE",
  "score": 50,
  "reasons": ["Applicant meets strong approval criteria"],
  "matchedRules": ["approve-qualified"]
}
```

### Step 4: Test Rule Evaluation - REJECT Case

Submit an applicant with low income (should reject):

```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-002",
    "age": 28,
    "income": 20000,
    "creditScore": 700,
    "debtToIncome": 0.25,
    "hasLatePayments": false,
    "requestedAmount": 100000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected response:**
```json
{
  "decision": "REJECT",
  "score": 0,
  "reasons": ["Income below minimum threshold of $30,000"],
  "matchedRules": ["income-check"]
}
```

### Step 5: Test Rule Evaluation - REVIEW Case

Submit an applicant with late payments (flags for manual review):

```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-003",
    "age": 45,
    "income": 55000,
    "creditScore": 680,
    "debtToIncome": 0.35,
    "hasLatePayments": true,
    "requestedAmount": 150000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected response:**
```json
{
  "decision": "REVIEW",
  "score": 0,
  "reasons": ["Applicant has history of late payments"],
  "matchedRules": ["late-payments-check"]
}
```

### Step 6: Verify Bias Prevention - Same Financials, Different Demographics

Submit two applicants with **identical financial metrics** but different ages:

**Applicant A (Young):**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-young",
    "age": 25,
    "income": 65000,
    "creditScore": 750,
    "debtToIncome": 0.28,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Applicant B (Older):**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-old",
    "age": 65,
    "income": 65000,
    "creditScore": 750,
    "debtToIncome": 0.28,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "RETIRED"
  }'
```

**Expected:** Both return `APPROVE` with score `50` and same reasons
- This proves age/employment status do NOT influence decisions

### Step 7: Run Backend Tests

```bash
cd ruleengine
./mvnw test
```

**Tests verify:**
- BiasPreventionTest: Age doesn't influence decisions
- Same financials = same decision regardless of demographics

### Step 8: Start Python Audit Service

Open a new terminal:

```bash
cd audit
pip install -r requirements.txt
python app.py
```

**Expected output:**
```
 * Running on http://127.0.0.1:5000
 * Debug mode: on
```

### Step 9: Check Audit Reports

Now that decisions are stored in SQLite, run audit queries:

**Approval rates by income bracket:**
```bash
curl http://localhost:5000/api/audit/income
```

**Expected response:**
```json
{
  "income_brackets": {
    "low": {
      "range": "0-30000",
      "total": 1,
      "approved": 0,
      "approval_rate": 0.0
    },
    "mid": {
      "range": "30000-50000",
      "total": 1,
      "approved": 0,
      "approval_rate": 0.0
    },
    "high": {
      "range": "50000+",
      "total": 2,
      "approved": 2,
      "approval_rate": 1.0
    }
  }
}
```

**Approval rates by age group:**
```bash
curl http://localhost:5000/api/audit/age
```

**Check for disparities:**
```bash
curl http://localhost:5000/api/audit/disparities
```

**Full audit report:**
```bash
curl http://localhost:5000/api/audit/report
```

### Step 10: Full End-to-End Test Flow

```bash
# 1. Backend running (terminal 1)
cd ruleengine
./mvnw spring-boot:run

# 2. Submit multiple applications (terminal 2)
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{"applicantID": "test-1", "age": 30, "income": 60000, "creditScore": 750, "debtToIncome": 0.30, "hasLatePayments": false, "requestedAmount": 200000, "employmentStatus": "EMPLOYED"}'

curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{"applicantID": "test-2", "age": 50, "income": 60000, "creditScore": 750, "debtToIncome": 0.30, "hasLatePayments": false, "requestedAmount": 200000, "employmentStatus": "EMPLOYED"}'

# 3. Start audit service (terminal 3)
cd audit
pip install -r requirements.txt
python app.py

# 4. Check fairness (terminal 2)
curl http://localhost:5000/api/audit/age
curl http://localhost:5000/api/audit/disparities
```

**Expected Result:** Both applicants approved regardless of age (proves fairness)

---

## Architecture 

This system demonstrates:

✅ **Full-Stack Architecture**
- Java backend (Spring Boot, JPA, REST APIs)
- Python analytics (statistical analysis)
- SQLite persistence (database design)

✅ **Bias Prevention by Design**
- Separation of decision logic from audit logic
- Field-level access control (enum restricts to financial metrics)
- Immutable audit trail (compliance-ready)

✅ **Explainability**
- Every decision includes reasons
- Full trace of rule matches
- Repeatable evaluation logic

✅ **Regulatory Awareness**
- Fair Lending Laws (ECOA, Dodd-Frank)
- Audit trails for compliance
- Disparity detection alerts

