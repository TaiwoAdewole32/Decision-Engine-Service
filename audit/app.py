from flask import Flask, jsonify
from bias_audit import BiasAudit

app = Flask(__name__)
audit = BiasAudit(db_path="../decisions.db")

@app.route("/api/audit/income", methods=["GET"])
def income_analysis():
    return jsonify(audit.analyze_approval_rate_by_income())

@app.route("/api/audit/age", methods=["GET"])
def age_analysis():
    return jsonify(audit.analyze_approval_rate_by_age())

@app.route("/api/audit/credit-score", methods=["GET"])
def credit_score_analysis():
    return jsonify(audit.analyze_approval_rate_by_credit_score())

@app.route("/api/audit/employment", methods=["GET"])
def employment_analysis():
    return jsonify(audit.analyze_approval_rate_by_employment())

@app.route("/api/audit/disparities", methods=["GET"])
def disparities():
    return jsonify(audit.detect_disparities())

@app.route("/api/audit/report", methods=["GET"])
def full_report():
    return jsonify(audit.get_full_audit_report())

if __name__ == "__main__":
    app.run(port=5000, debug=True)
