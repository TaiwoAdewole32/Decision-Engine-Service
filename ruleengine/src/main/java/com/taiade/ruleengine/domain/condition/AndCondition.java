package com.taiade.ruleengine.domain.condition;

import com.taiade.ruleengine.domain.model.CaseData;

import java.util.List;
/**
 * AndCondition means all conditions must be true for the overall condition to be true.
 * Ex: age >= 18 AND income >= 50000
 */
public class AndCondition implements Condition {
    private final List<Condition> conditions;
    // Constructor
    public AndCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }
    
    @Override
    public boolean evaluate(CaseData data) {
        //If any condition is false, the AND condition is false
        for (Condition condition : conditions) {
            if (!condition.evaluate(data)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String explain(CaseData data) {
        StringBuilder explanation = new StringBuilder("AND Condition:\n");
        for (Condition condition : conditions) {
            explanation.append(" - ").append(condition.explain(data)).append("\n");
        }
        return explanation.toString();
    }
}