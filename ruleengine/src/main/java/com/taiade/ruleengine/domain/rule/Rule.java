package com.taiade.ruleengine.domain.rule;
import com.taiade.ruleengine.domain.condition.Condition;
import com.taiade.ruleengine.domain.rule.action.*;
import com.taiade.ruleengine.domain.model.*;
import java.util.List;
/**
 * Rule represents a business rule in the rule engine.
 * Each rule has:
 * - An ID
 * - A condition that determines if the rule matches
 * - A list of actions to perform if the rule matches
 * - A priority to determine the order of rule evaluation
 * - A flag to indicate if rule evaluation should stop on match
 */
public class Rule {

    private final String id;
    private final Condition condition;
    private final List<RuleAction> actions;
    private final int priority;
    private final boolean stopOnMatch;

    public Rule(String id, Condition condition, List<RuleAction> actions, int priority, boolean stopOnMatch) {
        this.id = id;
        this.condition = condition;
        this.actions = actions;
        this.priority = priority;
        this.stopOnMatch = stopOnMatch;
    }

    public String getId() {
        return id;
    }

    public Condition getCondition() {
        return condition;
    }

    public List<RuleAction> getActions() {
        return actions;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isStopOnMatch() {
        return stopOnMatch;
    }

    // Check if the rule matches given the case data
    public boolean matches (CaseData data){
        return condition.evaluate(data);
    }

    // Apply all actions of the rule to the decision context
    public void applyActions(DecisionContext context){
        for (RuleAction action : actions) {
            action.apply(context);
        }
    }

    // Explain the rule's condition and actions
    public String explain(CaseData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rule ID: ").append(id).append("\n");
        sb.append("Priority: ").append(priority).append("\n");
        sb.append("Condition Explanation:\n").append(condition.explain(data)).append("\n");
        sb.append("Actions:\n");
        for (RuleAction action : actions) {
            sb.append(" - ").append(action.getClass().getSimpleName()).append("\n");
        }
        return sb.toString();
    }
}
