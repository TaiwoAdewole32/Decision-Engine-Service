package com.taiade.ruleengine.domain.condition;

import com.taiade.ruleengine.domain.model.CaseData;
/**
 * NotCondition inverts the result of another condition.
 * Ex: NOT (age >= 18) means age < 18
 */
public class NotCondition implements Condition {

    private final Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(CaseData data) {
        //Flip the result
        return !condition.evaluate(data);
    }

    @Override
    public String explain(CaseData data) {
        return "NOT (" + condition.explain(data) + ")";
    }
}