package com.taiade.ruleengine.domain.model;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;

public class DecisionResult{

    @NotNull
    private Decision decision;

    private Integer score;

    @NotNull
    private List<String> reasons = new ArrayList<>();

    @NotNull
    private List<String> matchedRulesIDs = new ArrayList<>();

    //List<TraceEntry> trace;

    public DecisionResult(){

    }

    public DecisionResult(Decision decision){
        this.decision = decision;
    }

    public Decision getDecision() {
        return decision;
    }

     public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public List<String> getMatchedRuleIDs() {
        return matchedRuleIDs;
    }

}