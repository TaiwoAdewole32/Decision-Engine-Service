package com.taiade.ruleengine.domain.rule.action;

import com.taiade.ruleengine.domain.rule.*;

/**
 * AddReasonAction is something a rule can do when it matches.
 * Ex: If a rule matches, we want to explain why: "Applicant has a high debt-to-income ratio"
 * Adds explanation into the DecisionContext.
 */
public class AddReasonAction implements RuleAction {
    
    private final String reason;
    
    public AddReasonAction(String reason) {
        this.reason = reason;
    }

    @Override
    public void apply(DecisionContext context){
        //Adds a readable reason to the running list of reasons
        context.addReason(reason);
    }

   
}
