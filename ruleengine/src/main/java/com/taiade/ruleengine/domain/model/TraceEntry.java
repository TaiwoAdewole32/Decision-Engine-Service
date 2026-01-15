package com.taiade.ruleengine.domain.model;
/**
 * TraceEntry captures the evaluation details of a single rule within the rule engine.
 * It records whether the rule was matched and any relevant details for auditing or debugging.
 * Stores: which rule was evaluated, whether it matched, and extra details on what happened
 */
public class TraceEntry{

    private String ruleID;
    private boolean matched;
    private String details;

     public TraceEntry(String ruleID, boolean matched, String details) {
        this.ruleID = ruleID;
        this.matched = matched;
        this.details = details;
    }

    // Getters
    public String getRuleID() {
        return ruleID;
    }

    public boolean isMatched() {
        return matched;
    }

    public String getDetails() {
        return details;
    }
}