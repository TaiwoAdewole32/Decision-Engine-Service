package com.taiade.ruleengine.domain.decision;
import java.util.List;
import java.util.ArrayList;

/**
 * DecisionContext is the "working memory" while rules are being evaluated/applied.
 * It holds the current decision, score, reasons, and matched rule IDs.
 * 
 */

public class DecisionContext {

    //Current decision
    private Decision decision;
    private int score;
    private final List<String> reasons = new ArrayList<>();
    private final List<String> matchedRulesIDs = new ArrayList<>();
    private final List<TraceEntry> trace = new ArrayList<>();

    @Override
    public String toString() {
        return "DecisionContext{" +
                "decision=" + decision +
                ", score=" + score +
                ", reasons=" + reasons +
                ", matchedRulesIDs=" + matchedRulesIDs +
                '}';
    }

    
    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
       if (this.score < 0) {
            this.score = 0; // Ensure score doesn't go negative
        }
        if (this.score > 200){
            this.score = 200; // Cap score at 200
        }
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void addReason(String reason) {
        reasons.add(reason);
    }

    public List<String> getMatchedRulesIDs() {
        return matchedRulesIDs;
    }

    //Record a specific rule matched
    public void addMatchedRulesID(String ruleID) {
        matchedRulesIDs.add(ruleID);
    }

    public List<TraceEntry> getTrace() {
        return trace;
    }

    public void addTrace(TraceEntry entry) {
        trace.add(entry);
    }
}
