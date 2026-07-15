package com.taiade.ruleengine;

import org.junit.jupiter.api.Test;
import com.taiade.ruleengine.domain.condition.ComparisonCondition;
import com.taiade.ruleengine.domain.condition.ComparisonCondition.Field;
import com.taiade.ruleengine.domain.condition.Operator;
import com.taiade.ruleengine.domain.decision.Decision;
import com.taiade.ruleengine.domain.decision.DecisionResult;
import com.taiade.ruleengine.domain.model.CaseData;
import com.taiade.ruleengine.domain.rule.Rule;
import com.taiade.ruleengine.domain.rule.RuleEngine;
import com.taiade.ruleengine.domain.rule.action.SetDecisionAction;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;



class BiasPreventionTest {

    @Test
    void testDemographicDataNeverInfluencesRules() {
        Rule incomeRule = new Rule(
            "income-check",
            new ComparisonCondition(ComparisonCondition.Field.INCOME, Operator.LESS_THAN, 30000),
            Arrays.asList(new SetDecisionAction(Decision.REJECT)),
            1,
            false
        );

        RuleEngine engine = new RuleEngine(Arrays.asList(incomeRule));

        CaseData applicant1 = new CaseData(
            "app-1",
            25,
            25000,
            750,
            0.3,
            false,
            100000,
            true
        );

        CaseData applicant2 = new CaseData(
            "app-2",
            60,
            25000,
            750,
            0.3,
            false,
            100000,
            true
        );

        DecisionResult result1 = engine.evaluate(applicant1);
        DecisionResult result2 = engine.evaluate(applicant2);

        assertEquals(Decision.REJECT, result1.getDecision(), "Age 25 with low income should be rejected");
        assertEquals(Decision.REJECT, result2.getDecision(), "Age 60 with same low income should also be rejected - decision should not change based on age");
    }

    @Test
    void testOnlyObjectiveFinancialMetricsInRules() {
        Rule approvalRule = new Rule(
            "approval-rule",
            new ComparisonCondition(ComparisonCondition.Field.INCOME, Operator.GREATER_THAN_OR_EQUALS, 50000),
            Arrays.asList(new SetDecisionAction(Decision.APPROVE)),
            1,
            true
        );

        RuleEngine engine = new RuleEngine(Arrays.asList(approvalRule));

        CaseData highIncomeYoung = new CaseData(
            "app-young",
            22,
            65000,
            700,
            0.3,
            false,
            100000,
            true
        );

        CaseData highIncomeOld = new CaseData(
            "app-old",
            70,
            65000,
            700,
            0.3,
            false,
            100000,
            false
        );

        DecisionResult resultYoung = engine.evaluate(highIncomeYoung);
        DecisionResult resultOld = engine.evaluate(highIncomeOld);

        assertEquals(Decision.APPROVE, resultYoung.getDecision());
        assertEquals(Decision.APPROVE, resultOld.getDecision());
        assertEquals(resultYoung.getDecision(), resultOld.getDecision(), "Decision must be identical for same income regardless of age or employment status");
    }

    @Test
    void testValidRuleFieldsAreFinancialOnly() {
        Field[] validFields = Field.values();

        for (Field field : validFields) {
            String fieldName = field.name().toLowerCase();
            assertTrue(
                fieldName.contains("applicant_id") ||
                fieldName.contains("income") ||
                fieldName.contains("age") ||
                fieldName.contains("credit_score") ||
                fieldName.contains("debt_to_income") ||
                fieldName.contains("has_late_payments") ||
                fieldName.contains("requested_amount") ||
                fieldName.contains("employment_status"),
                "Field " + fieldName + " should be objective financial metric, not demographic identifier"
            );
        }
    }
}
