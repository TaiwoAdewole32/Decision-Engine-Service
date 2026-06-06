# Project: Fair-Loan Decision Engine 

## Overview: 
A fairness-aware loan decision engine built with Java and Spring Boot. This project evaluates loan applications  using configurable business rules, generates explainable approval decisions, and is being designed to support bias auditing for more transparent lending workflows. Loan approval systems can affect major life opportunities, but many decision systems are difficult to explain or audit. FairLoan Decision Engine is a backend service that simulates how a lender could evaluate loan applications while keeping decision logic transparent, traceable, and easier to review for potential bias. The goal of this project is not just to approve or reject applications. The goal is to build a decision system that can explain **why** a decision was made and eventually help identify whether certain groups are being unfairly impacted.

## Key Features
- Submit loan application data for evaluation
- Evaluate applications using rule-based decision logic
- Support approval outcomes such as `APPROVE`, `REJECT`, and `REVIEW`
- Add score-based decision factors
- Generate human-readable reasons for decisions
- Track which rules matched during evaluation
- Support compound conditions such as `AND`, `OR`, and `NOT`
- Designed with future fairness auditing and full-stack expansion in mind

## Why This Project Matters
Many financial technology systems rely on automated or semi-automated decision-making. If those systems are not transparent, they can be difficult to debug, explain, or audit for unfair outcomes.

## Tech Stack
- **Java**
- **Spring Boot**
- **Maven**
- **JUnit**
- **Jakarta Validation**
- **REST API design**
- **Object-Oriented Programming**
- **Rule-based system design**

## Current Architecture

The project is organized around a rule engine pattern.

```txt
 Loan Application
       |
       v
   CaseData
       |
       v
  RuleEngine
       |
       v
Rules + Conditions
       |
       v
   Rule Actions
       |
       v
  DecisionResult
