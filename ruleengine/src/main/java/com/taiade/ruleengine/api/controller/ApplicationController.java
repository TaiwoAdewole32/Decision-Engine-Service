package com.taiade.ruleengine.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taiade.ruleengine.domain.decision.DecisionResult;
import com.taiade.ruleengine.domain.model.CaseData;
import com.taiade.ruleengine.domain.model.DecisionRecord;
import com.taiade.ruleengine.domain.rule.RuleEngine;
import com.taiade.ruleengine.infrastructure.repository.DecisionRepository;
import com.taiade.ruleengine.infrastructure.repository.CaseDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private DecisionRepository decisionRepository;

    @Autowired
    private CaseDataRepository caseDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateApplication(@Valid @RequestBody CaseData application) {
        try {
            CaseData saved = caseDataRepository.save(application);

            DecisionResult result = ruleEngine.evaluate(application);

            DecisionRecord record = new DecisionRecord(
                application.getApplicantId(),
                result.getDecision(),
                result.getScore(),
                objectMapper.writeValueAsString(result.getReasons()),
                objectMapper.writeValueAsString(result.getMatchedRulesIDs()),
                objectMapper.writeValueAsString(result.getTrace()),
                application.getAge(),
                application.getIncome(),
                application.getCreditScore(),
                application.isEmployed()
            );

            decisionRepository.save(record);

            return ResponseEntity.ok(new EvaluationResponse(result, record.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{applicantID}")
    public ResponseEntity<?> getApplicationHistory(@PathVariable String applicantID) {
        var decisions = decisionRepository.findByApplicantID(applicantID);
        if (decisions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(decisions);
    }

    public static class EvaluationResponse {
        public String decision;
        public Integer score;
        public Object reasons;
        public Object matchedRules;
        public Long recordId;

        public EvaluationResponse(DecisionResult result, Long recordId) {
            this.decision = result.getDecision().toString();
            this.score = result.getScore();
            this.reasons = result.getReasons();
            this.matchedRules = result.getMatchedRulesIDs();
            this.recordId = recordId;
        }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
