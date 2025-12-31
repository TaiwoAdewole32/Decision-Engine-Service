package com.taiade.ruleengine.domain.condition;

import com.taiade.ruleengine.domain.model.CaseData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionManualTest {

    @Test
    void andCondition_exampleRun() {
        // 1. Create input data
        CaseData data = new CaseData();
        data.setCreditScore(690);
        data.setIncome(42000);
        data.setHasLatePayments(false);

        // 2. Build condition
        Condition condition = new AndCondition(List.of(
                new ComparisonCondition("creditScore", Operator.GTE, 680),
                new ComparisonCondition("income", Operator.GTE, 30000),
                new NotCondition(
                        new ComparisonCondition("hasLatePayments", Operator.EQ, true)
                )
        ));

        // 3. Evaluate
        boolean result = condition.evaluate(data);

        // 4. Assert
        assertTrue(result);

        // Optional: print explanation
        System.out.println(condition.explain(data));
    }
}