package com.taiade.ruleengine.domain.condition;

import com.taiade.ruleengine.domain.model.CaseData;

import java.util.List;

public class OrCondition implements Condition {

    private final List<Condition> conditions;

    public OrCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(CaseData data) {
        for (Condition condition : conditions) {
            if (condition.evaluate(data)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String explain(CaseData data) {
        StringBuilder sb = new StringBuilder("OR condition:\n");
        for (Condition condition : conditions) {
            sb.append(" - ").append(condition.explain(data)).append("\n");
        }
        return sb.toString();
    }
}