package com.taiade.ruleengine.domain.rule;

import java.util.List;

import com.taiade.ruleengine.domain.decision.Decision;
import com.taiade.ruleengine.domain.decision.DecisionContext;
import com.taiade.ruleengine.domain.decision.DecisionResult;
import com.taiade.ruleengine.domain.model.CaseData;

import java.util.Collections;

public class RuleEngine {
    
    private List<Rule> rules;

    public RuleEngine(List<Rule> rules) {
        this.rules = rules;
        // Sort rules by their priority. Rule must provide getPriority().
        Collections.sort(this.rules, (r1, r2) -> Integer.compare(r1.getPriority(), r2.getPriority()));
    }

    public DecisionResult evaluate(CaseData data){
        DecisionContext context = new DecisionContext();
        for (Rule rule : rules) {
            if (rule.matches(data)) {
                rule.applyActions(context);
                context.addMatchedRulesID(rule.getId());
                if (rule.isStopOnMatch()) {
                    break; // Stop evaluating further rules if this rule matches and is set to stop on match
                }
            }
        }
        return context.getDecision() == null ? new DecisionResult(Decision.REVIEW, 0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()) : new DecisionResult(context.getDecision(), context.getScore(), context.getReasons(), context.getMatchedRulesIDs(), context.getTrace());
    }

}
