# Bias Auditing Service

This service analyzes loan decisions for algorithmic bias by examining approval patterns across demographic groups.

## Key Principle

**Demographic data NEVER influences rule evaluation.** This is enforced in the Java backend. The audit service reads decision logs and analyzes patterns AFTER decisions are made.

## Running the Audit Service

```bash
cd audit
pip install -r requirements.txt
python app.py
```

Service runs on `http://localhost:5000`

## Endpoints

### GET `/api/audit/income`
Approval rate by income bracket:
- <30k
- 30k-50k
- 50k-75k
- 75k-100k
- >100k

### GET `/api/audit/age`
Approval rate by age group:
- <25
- 25-34
- 35-44
- 45-54
- 55+

### GET `/api/audit/credit-score`
Approval rate by credit score range:
- <620
- 620-659
- 660-699
- 700-739
- 740+

### GET `/api/audit/employment`
Approval rate by employment status.

### GET `/api/audit/disparities`
Detects significant approval rate differences (>10% by default). Returns severity levels:
- **MEDIUM**: 10-20% difference
- **HIGH**: >20% difference

### GET `/api/audit/report`
Full audit report combining all analyses.

## Example Response

```json
{
  "income_analysis": {
    "metric": "Approval Rate by Income Bracket",
    "data": [
      {
        "bracket": "<30k",
        "total": 150,
        "approved": 45,
        "approval_rate": 30.0
      },
      {
        "bracket": "50k-75k",
        "total": 200,
        "approved": 160,
        "approval_rate": 80.0
      }
    ]
  },
  "disparities": {
    "disparities_found": 1,
    "disparities": [
      {
        "category": "Income",
        "max_rate": 80.0,
        "min_rate": 30.0,
        "difference": 50.0,
        "severity": "HIGH"
      }
    ]
  }
}
```

## Interpreting Results

- **Disparities are RED FLAGS**: If a 50% difference exists between income groups, the company must investigate why.
- **Not all disparities are bias**: There may be legitimate financial reasons (e.g., higher earners have lower debt-to-income ratios).
- **Regular audits required**: Run this service regularly to monitor for emerging patterns.

## Database Schema

All data comes from the `decisions` SQLite table populated by the Java backend:

```
id | applicantID | decision | score | reasons | matchedRules | trace | age | income | creditScore | employmentStatus | timestamp
```

Demographics (age, income, creditScore, employmentStatus) are **stored only for audit purposes**, never used in rule evaluation.
