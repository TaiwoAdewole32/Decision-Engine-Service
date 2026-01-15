package com.taiade.ruleengine.domain.model;
import com.taiade.ruleengine.domain.decision.Decision;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;


/**
 * DecisionResult is the output of the rule engine after evaluating all rules against the provided case data.
 * It encapsulates the final decision, the cumulative score, reasons for the decision, and the IDs of the rules that were matched.
 */
public class DecisionResult{

    //The final decision (e.g., APPROVE, REJECT, REVIEW)
    @NotNull
    private Decision decision;

    //The final total score after rules add/subtract points
    @NotNull
    private Integer score;

    //Explanations for the decision
    @NotNull
    private List<String> reasons = new ArrayList<>();

    //IDs of the rules that were matched during evaluation
    @NotNull
    private List<String> matchedRulesIDs = new ArrayList<>();

    //Store detailed trace of each rule evaluation for debugging/auditing purposes
    @NotNull
    private List<TraceEntry> trace;
    
    //Empty constructor needed for frameworks
    public DecisionResult(){
    
    }

    //Create a result with a decision already set
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

    public List<String> getMatchedRulesIDs() {
        return matchedRulesIDs;
    }

}