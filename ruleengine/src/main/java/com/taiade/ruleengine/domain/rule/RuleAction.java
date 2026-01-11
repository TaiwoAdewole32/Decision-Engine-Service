package com.taiade.ruleengine.domain.rule;


public interface RuleAction {
    void apply(DecisionContext context);
}
