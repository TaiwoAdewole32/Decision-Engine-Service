package com.taiade.ruleengine.domain.model;
import jakarta.validation.constraints.*;
/**
 * CaseData is the input to the rule engine. It contains all relevant information about a loan application
 * that rules will evaluate to arrive at a decision.
 */
public class CaseData{
    
    @NotNull
    private String applicantID;

    @NotNull
    @Min(18)
    private Integer age;

    @NotNull
    @Min(0)
    private Integer income;
    @NotNull
    @Min(300)
    @Max(850)
    private Integer creditScore;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double debtToIncome;

    @NotNull
    private Boolean hasLatePayments;

    @NotNull
    @Min(1)
    private Integer requestedAmount;
    
    public CaseData(String applicantID, Integer age, Integer income, Integer creditScore, Double debtToIncome, Boolean hasLatePayments, Integer requestedAmount) {
        this.applicantID = applicantID;
        this.age = age;
        this.income = income;
        this.creditScore = creditScore;
        this.debtToIncome = debtToIncome;
        this.hasLatePayments = hasLatePayments;
        this.requestedAmount = requestedAmount;
    }

    
    // Getters and setters
    public String getApplicantId() {
        return applicantID;
    }

    public void setApplicantId(String applicantID) {
        this.applicantID = applicantID;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public Double getDebtToIncome() {
        return debtToIncome;
    }

    public void setDebtToIncome(Double debtToIncome) {
        this.debtToIncome = debtToIncome;
    }

    public Boolean getHasLatePayments() {
        return hasLatePayments;
    }

    public void setHasLatePayments(Boolean hasLatePayments) {
        this.hasLatePayments = hasLatePayments;
    }

    public Integer getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Integer requestedAmount) {
        this.requestedAmount = requestedAmount;
    }
}