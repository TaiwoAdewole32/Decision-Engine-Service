package com.taiade.ruleengine.domain.condition;

import com.taiade.ruleengine.domain.model.CaseData;

public class NotCondition implements Condition {

    private final Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(CaseData data) {
        return !condition.evaluate(data);
    }

    @Override
    public String explain(CaseData data) {
        return "NOT (" + condition.explain(data) + ")";
    }
}