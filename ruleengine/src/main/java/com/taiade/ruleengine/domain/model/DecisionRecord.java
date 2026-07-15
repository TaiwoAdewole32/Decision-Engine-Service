package com.taiade.ruleengine.domain.model;

import jakarta.persistence.*;
import com.taiade.ruleengine.domain.decision.Decision;
import java.time.LocalDateTime;

@Entity
@Table(name = "decisions")
public class DecisionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicantID;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Decision decision;

    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String reasons;

    @Column(columnDefinition = "TEXT")
    private String matchedRules;

    @Column(columnDefinition = "TEXT")
    private String trace;

    private Integer age;
    private Integer income;
    private Integer creditScore;
    private Boolean employed;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public DecisionRecord() {}

    public DecisionRecord(String applicantID, Decision decision, Integer score, String reasons, String matchedRules, String trace, Integer age, Integer income, Integer creditScore, Boolean employed) {
        this.applicantID = applicantID;
        this.decision = decision;
        this.score = score;
        this.reasons = reasons;
        this.matchedRules = matchedRules;
        this.trace = trace;
        this.age = age;
        this.income = income;
        this.creditScore = creditScore;
        this.employed = employed;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getApplicantID() {
        return applicantID;
    }

    public Decision getDecision() {
        return decision;
    }

    public Integer getScore() {
        return score;
    }

    public String getReasons() {
        return reasons;
    }

    public String getMatchedRules() {
        return matchedRules;
    }

    public String getTrace() {
        return trace;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getIncome() {
        return income;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public Boolean isEmployed() {
        return employed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
