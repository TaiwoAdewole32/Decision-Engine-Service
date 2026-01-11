package com.taiade.ruleengine.domain.rule.action;

import com.taiade.ruleengine.domain.rule.*;


public class AddReasonAction implements RuleAction {
    private final String reason;
    
    public AddReasonAction(String reason) {
        this.reason = reason;
    }

    @Override
    public void apply(DecisionContext context){
        context.addReason(reason);
    }

   
}
