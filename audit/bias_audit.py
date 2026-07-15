import sqlite3
from typing import Dict, List, Any
from datetime import datetime
from collections import defaultdict

class BiasAudit:
    def __init__(self, db_path: str = "decisions.db"):
        self.db_path = db_path

    def _get_connection(self):
        return sqlite3.connect(self.db_path)

    def analyze_approval_rate_by_income(self) -> Dict[str, Any]:
        """Analyze approval rates by income brackets."""
        conn = self._get_connection()
        cursor = conn.cursor()

        cursor.execute("""
            SELECT
                CASE
                    WHEN income < 30000 THEN '<30k'
                    WHEN income < 50000 THEN '30k-50k'
                    WHEN income < 75000 THEN '50k-75k'
                    WHEN income < 100000 THEN '75k-100k'
                    ELSE '>100k'
                END as income_bracket,
                COUNT(*) as total,
                SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) as approved,
                ROUND(100.0 * SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) / COUNT(*), 2) as approval_rate
            FROM decisions
            GROUP BY income_bracket
            ORDER BY income;
        """)

        results = cursor.fetchall()
        conn.close()

        return {
            "metric": "Approval Rate by Income Bracket",
            "data": [
                {
                    "bracket": row[0],
                    "total": row[1],
                    "approved": row[2],
                    "approval_rate": row[3]
                } for row in results
            ]
        }

    def analyze_approval_rate_by_age(self) -> Dict[str, Any]:
        """Analyze approval rates by age groups."""
        conn = self._get_connection()
        cursor = conn.cursor()

        cursor.execute("""
            SELECT
                CASE
                    WHEN age < 25 THEN '<25'
                    WHEN age < 35 THEN '25-34'
                    WHEN age < 45 THEN '35-44'
                    WHEN age < 55 THEN '45-54'
                    ELSE '55+'
                END as age_group,
                COUNT(*) as total,
                SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) as approved,
                ROUND(100.0 * SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) / COUNT(*), 2) as approval_rate
            FROM decisions
            GROUP BY age_group
            ORDER BY age;
        """)

        results = cursor.fetchall()
        conn.close()

        return {
            "metric": "Approval Rate by Age Group",
            "data": [
                {
                    "group": row[0],
                    "total": row[1],
                    "approved": row[2],
                    "approval_rate": row[3]
                } for row in results
            ]
        }

    def analyze_approval_rate_by_credit_score(self) -> Dict[str, Any]:
        """Analyze approval rates by credit score ranges."""
        conn = self._get_connection()
        cursor = conn.cursor()

        cursor.execute("""
            SELECT
                CASE
                    WHEN creditScore < 620 THEN '<620'
                    WHEN creditScore < 660 THEN '620-659'
                    WHEN creditScore < 700 THEN '660-699'
                    WHEN creditScore < 740 THEN '700-739'
                    ELSE '740+'
                END as score_range,
                COUNT(*) as total,
                SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) as approved,
                ROUND(100.0 * SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) / COUNT(*), 2) as approval_rate
            FROM decisions
            GROUP BY score_range
            ORDER BY creditScore;
        """)

        results = cursor.fetchall()
        conn.close()

        return {
            "metric": "Approval Rate by Credit Score Range",
            "data": [
                {
                    "range": row[0],
                    "total": row[1],
                    "approved": row[2],
                    "approval_rate": row[3]
                } for row in results
            ]
        }

    def analyze_approval_rate_by_employment(self) -> Dict[str, Any]:
        """Analyze approval rates by employment status."""
        conn = self._get_connection()
        cursor = conn.cursor()

        cursor.execute("""
            SELECT
                employmentStatus,
                COUNT(*) as total,
                SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) as approved,
                ROUND(100.0 * SUM(CASE WHEN decision = 'APPROVE' THEN 1 ELSE 0 END) / COUNT(*), 2) as approval_rate
            FROM decisions
            GROUP BY employmentStatus
            ORDER BY approval_rate DESC;
        """)

        results = cursor.fetchall()
        conn.close()

        return {
            "metric": "Approval Rate by Employment Status",
            "data": [
                {
                    "status": row[0],
                    "total": row[1],
                    "approved": row[2],
                    "approval_rate": row[3]
                } for row in results
            ]
        }

    def analyze_average_loan_amount_by_income(self) -> Dict[str, Any]:
        """Analyze average requested loan amounts by income bracket."""
        # Note: This requires extending DecisionRecord to store requestedAmount
        return {"error": "Requires schema update to store requestedAmount in decisions table"}

    def detect_disparities(self, threshold_percent: float = 10.0) -> Dict[str, Any]:
        """Detect significant approval rate disparities."""
        income_data = self.analyze_approval_rate_by_income()
        age_data = self.analyze_approval_rate_by_age()
        employment_data = self.analyze_approval_rate_by_employment()

        disparities = []

        # Check income disparities
        income_rates = [d["approval_rate"] for d in income_data["data"] if d["approval_rate"] is not None]
        if income_rates:
            max_rate = max(income_rates)
            min_rate = min(income_rates)
            diff = max_rate - min_rate
            if diff > threshold_percent:
                disparities.append({
                    "category": "Income",
                    "max_rate": max_rate,
                    "min_rate": min_rate,
                    "difference": diff,
                    "severity": "HIGH" if diff > 20 else "MEDIUM"
                })

        # Check age disparities
        age_rates = [d["approval_rate"] for d in age_data["data"] if d["approval_rate"] is not None]
        if age_rates:
            max_rate = max(age_rates)
            min_rate = min(age_rates)
            diff = max_rate - min_rate
            if diff > threshold_percent:
                disparities.append({
                    "category": "Age",
                    "max_rate": max_rate,
                    "min_rate": min_rate,
                    "difference": diff,
                    "severity": "HIGH" if diff > 20 else "MEDIUM"
                })

        # Check employment disparities
        emp_rates = [d["approval_rate"] for d in employment_data["data"] if d["approval_rate"] is not None]
        if emp_rates:
            max_rate = max(emp_rates)
            min_rate = min(emp_rates)
            diff = max_rate - min_rate
            if diff > threshold_percent:
                disparities.append({
                    "category": "Employment Status",
                    "max_rate": max_rate,
                    "min_rate": min_rate,
                    "difference": diff,
                    "severity": "HIGH" if diff > 20 else "MEDIUM"
                })

        return {
            "timestamp": datetime.now().isoformat(),
            "threshold_percent": threshold_percent,
            "disparities_found": len(disparities),
            "disparities": disparities
        }

    def get_full_audit_report(self) -> Dict[str, Any]:
        """Generate comprehensive audit report."""
        return {
            "generated_at": datetime.now().isoformat(),
            "income_analysis": self.analyze_approval_rate_by_income(),
            "age_analysis": self.analyze_approval_rate_by_age(),
            "credit_score_analysis": self.analyze_approval_rate_by_credit_score(),
            "employment_analysis": self.analyze_approval_rate_by_employment(),
            "disparities": self.detect_disparities()
        }
