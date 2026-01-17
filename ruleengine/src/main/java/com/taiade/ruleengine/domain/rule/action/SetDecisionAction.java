package com.taiade.ruleengine.domain.rule.action;
import com.taiade.ruleengine.domain.rule.*;
import com.taiade.ruleengine.domain.decision.*;;

/**
 * SetDecisionAction is something a rule can do when it matches.
 * Ex: If a rule matches, we want to set the final decision: APPROVE, REJECT, or REVIEW
 * Sets the decision in the DecisionContext.
 */
public class SetDecisionAction implements RuleAction {
    
    private final Decision decision;

    public SetDecisionAction(Decision decision) {
        this.decision = decision;
    }

    public void apply(DecisionContext context){
        //Sets the decision in the DecisionContext
        context.setDecision(decision);
    }
}

   
