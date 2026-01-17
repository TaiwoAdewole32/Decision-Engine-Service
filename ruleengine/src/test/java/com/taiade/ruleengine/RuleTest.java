package com.taiade.ruleengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.taiade.ruleengine.domain.decision.Decision;
import com.taiade.ruleengine.domain.model.CaseData;
import com.taiade.ruleengine.domain.rule.DecisionContext;
import com.taiade.ruleengine.domain.rule.Rule;
import com.taiade.ruleengine.domain.rule.action.AddReasonAction;
import com.taiade.ruleengine.domain.rule.action.SetDecisionAction;
import com.taiade.ruleengine.domain.condition.Operator;
import com.taiade.ruleengine.domain.condition.ComparisonCondition;


import java.util.List;

@SpringBootTest
public class RuleTest {
    @Test
    void test1(){

        CaseData caseData = new CaseData(
                "John Doe",
                30,
                60000,
                620,
                0.35,
                false,
                15000
        );

        Rule rejectLowCredit = new Rule("LowCreditScoreRule",
                 new ComparisonCondition("creditScore", Operator.LESS_THAN, 650),
                List.of(
                        new AddReasonAction("Credit score too low"),
                        new SetDecisionAction(Decision.REJECT)
                ),
                1,
                true
        );

        DecisionContext decisionContext = new DecisionContext();
        boolean matched = rejectLowCredit.matches(caseData);
        if (matched) {
            rejectLowCredit.applyActions(decisionContext);
        }
        assertTrue(matched);
        assertEquals(Decision.REJECT, decisionContext.getDecision());
        assertTrue(decisionContext.getReasons().contains("Credit score too low"));

        
    }
}
