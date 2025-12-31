package com.taiade.ruleengine.domain.model;
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