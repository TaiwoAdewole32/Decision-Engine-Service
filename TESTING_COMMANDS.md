# Testing Commands - Fair Loan Decision Engine

## Quick Start (Copy & Paste)

### Terminal 1: Start Backend
```bash
cd ruleengine
./mvnw spring-boot:run
```

Wait for: `Tomcat started on port(s): 8080`

---

### Terminal 2: Test Backend (While backend running)

**Health check:**
```bash
curl http://localhost:8080/actuator/health
```

**Test 1: APPROVE (Strong applicant)**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-approve-1",
    "age": 35,
    "income": 65000,
    "creditScore": 750,
    "debtToIncome": 0.28,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected:** `"decision": "APPROVE", "score": 50`

---

**Test 2: REJECT (Low income)**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-reject-1",
    "age": 28,
    "income": 20000,
    "creditScore": 700,
    "debtToIncome": 0.25,
    "hasLatePayments": false,
    "requestedAmount": 100000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected:** `"decision": "REJECT"` with reason `"Income below minimum threshold of $30,000"`

---

**Test 3: REJECT (Low credit score)**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-reject-2",
    "age": 45,
    "income": 55000,
    "creditScore": 600,
    "debtToIncome": 0.35,
    "hasLatePayments": false,
    "requestedAmount": 150000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected:** `"decision": "REJECT"` with reason `"Credit score below minimum of 620"`

---

**Test 4: REVIEW (Late payments)**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-review-1",
    "age": 45,
    "income": 55000,
    "creditScore": 680,
    "debtToIncome": 0.35,
    "hasLatePayments": true,
    "requestedAmount": 150000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected:** `"decision": "REVIEW"` with reason `"Applicant has history of late payments"`

---

**Test 5: REVIEW (High debt ratio)**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "app-review-2",
    "age": 35,
    "income": 50000,
    "creditScore": 720,
    "debtToIncome": 0.50,
    "hasLatePayments": false,
    "requestedAmount": 150000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Expected:** `"decision": "REVIEW"` with reason `"Debt-to-income ratio above 43%"`

---

### Bias Prevention Test: Same Financials, Different Demographics

**Applicant A (Age 25):**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "bias-test-young",
    "age": 25,
    "income": 65000,
    "creditScore": 750,
    "debtToIncome": 0.28,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "EMPLOYED"
  }'
```

**Applicant B (Age 65):**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantID": "bias-test-old",
    "age": 65,
    "income": 65000,
    "creditScore": 750,
    "debtToIncome": 0.28,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "RETIRED"
  }'
```

**Expected:** Both return `APPROVE` with identical score and reasons ✅ (proves age doesn't influence decision)

---

### Run Backend Tests
```bash
cd ruleengine
./mvnw test
```

**Verifies:**
- BiasPreventionTest: Demographic data never influences rules
- Same financials = same decision regardless of age

---

### Terminal 3: Start Audit Service

```bash
cd audit
pip install -r requirements.txt
python app.py
```

Wait for: `Running on http://127.0.0.1:5000`

---

### Terminal 2: Test Audit Service

**Approval rates by income bracket:**
```bash
curl http://localhost:5000/api/audit/income | python -m json.tool
```

**Approval rates by age group:**
```bash
curl http://localhost:5000/api/audit/age | python -m json.tool
```

**Approval rates by credit score:**
```bash
curl http://localhost:5000/api/audit/credit-score | python -m json.tool
```

**Approval rates by employment status:**
```bash
curl http://localhost:5000/api/audit/employment | python -m json.tool
```

**Check for disparities (bias alerts):**
```bash
curl http://localhost:5000/api/audit/disparities | python -m json.tool
```

**Full audit report:**
```bash
curl http://localhost:5000/api/audit/report | python -m json.tool
```

---

## Expected Audit Output Example

After submitting test cases, approval rates should look like:

```
Income Analysis:
  <$30k:    0% approval (0 approved, 1 total)    ← All rejected (too low income)
  $30-50k:  0% approval (0 approved, 2 total)    ← Rejected (mixed reasons)
  >$50k:    100% approval (2 approved, 2 total)  ← All approved (strong metrics)

Age Analysis:
  18-30:    50% approval (1 approved, 2 total)   ← Same rate across ages ✅
  31-50:    50% approval (2 approved, 4 total)   ← (proves no age bias)
  51+:      50% approval (1 approved, 2 total)   ← (proves no age bias)

Employment Status Analysis:
  EMPLOYED: 50% approval (2 approved, 4 total)   ← Same rate ✅
  RETIRED:  50% approval (1 approved, 2 total)   ← (proves no employment bias)

Credit Score Analysis:
  300-620:  0% approval (0 approved, 2 total)    ← All rejected (too low credit)
  620-700:  50% approval (1 approved, 2 total)   ← Mixed results
  700+:     100% approval (2 approved, 2 total)  ← All approved
```

**Key Finding:** Age and employment do NOT correlate with approval rate → ✅ Fair system

---

## Troubleshooting

### Backend won't start
```bash
# Check if port 8080 is in use
netstat -ano | grep 8080

# Try killing the process (Windows)
taskkill /PID <PID> /F

# Or use different port
cd ruleengine
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Audit service won't start
```bash
# Check Python installation
python --version

# Reinstall dependencies
cd audit
pip install -r requirements.txt --force-reinstall

# Try different port
python app.py  # Edit app.py and change port 5000 to 5001
```

### Database errors
```bash
# Delete old database
rm ruleengine/decisions.db
rm audit/decisions.db

# Restart backend (will recreate schema)
cd ruleengine
./mvnw spring-boot:run
```

### Curl not found (Windows)
```bash
# Install with Chocolatey
choco install curl

# Or use PowerShell
Invoke-WebRequest -Uri http://localhost:8080/actuator/health
```

---

## Full Workflow (Copy & Paste All)

```bash
# Window 1: Start backend
cd ruleengine
./mvnw spring-boot:run

# Window 2: Wait 30 seconds, then run all tests
sleep 30
curl http://localhost:8080/actuator/health

curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{"applicantID": "test-1", "age": 35, "income": 65000, "creditScore": 750, "debtToIncome": 0.28, "hasLatePayments": false, "requestedAmount": 250000, "employmentStatus": "EMPLOYED"}'

curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{"applicantID": "test-2", "age": 28, "income": 20000, "creditScore": 700, "debtToIncome": 0.25, "hasLatePayments": false, "requestedAmount": 100000, "employmentStatus": "EMPLOYED"}'

# Window 3: Start audit service
cd audit
pip install -r requirements.txt
python app.py

# Window 2: Check audit results (after audit service starts)
sleep 10
curl http://localhost:5000/api/audit/age
curl http://localhost:5000/api/audit/income
curl http://localhost:5000/api/audit/disparities
```

