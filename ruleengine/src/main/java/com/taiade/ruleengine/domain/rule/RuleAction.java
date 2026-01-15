package com.taiade.ruleengine.domain.rule;

/**
 * RuleAction is the thing a rule does when it matches
 * Ex: If a rule matches, we might want to add points to the score, or add a reason, or set the decision to REJECT
 */
public interface RuleAction {
    void apply(DecisionContext context);
}
