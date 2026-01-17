package com.taiade.ruleengine.domain.rule.action;
import com.taiade.ruleengine.domain.rule.*;

/**
 * AddScoreAction is something a rule can do when it matches.
 * Ex: If a rule matches, we want to add points to the score: +10 points for low risk
 * Adds to the score in the DecisionContext.
 */
public class AddScoreAction implements RuleAction {

    private final int score;

    public AddScoreAction(int score) {
        this.score = score;
    }

    public void apply(DecisionContext context){
        //Adds to the running score in the DecisionContext
        context.addScore(score);
    }
}
