package com.taiade.ruleengine.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taiade.ruleengine.domain.condition.*;
import com.taiade.ruleengine.domain.decision.Decision;
import com.taiade.ruleengine.domain.rule.Rule;
import com.taiade.ruleengine.domain.rule.RuleAction;
import com.taiade.ruleengine.domain.rule.action.AddReasonAction;
import com.taiade.ruleengine.domain.rule.action.AddScoreAction;
import com.taiade.ruleengine.domain.rule.action.SetDecisionAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleLoader {

    @Autowired
    private ObjectMapper objectMapper;

    public List<Rule> loadRules(String configPath) throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream(configPath);
        JsonNode root = objectMapper.readTree(input);
        JsonNode rulesNode = root.get("rules");

        List<Rule> rules = new ArrayList<>();
        for (JsonNode ruleNode : rulesNode) {
            Rule rule = parseRule(ruleNode);
            rules.add(rule);
        }
        return rules;
    }

    private Rule parseRule(JsonNode ruleNode) throws Exception {
        String id = ruleNode.get("id").asText();
        int priority = ruleNode.get("priority").asInt();
        boolean stopOnMatch = ruleNode.get("stopOnMatch").asBoolean(false);

        Condition condition = parseCondition(ruleNode.get("condition"));

        List<RuleAction> actions = new ArrayList<>();
        JsonNode actionsNode = ruleNode.get("actions");
        for (JsonNode actionNode : actionsNode) {
            RuleAction action = parseAction(actionNode);
            actions.add(action);
        }

        return new Rule(id, condition, actions, priority, stopOnMatch);
    }

    private Condition parseCondition(JsonNode condNode) throws Exception {
        String type = condNode.get("type").asText();

        if ("comparison".equals(type)) {
            String fieldName = condNode.get("field").asText();
            String operator = condNode.get("operator").asText();
            Object threshold = condNode.get("threshold").numberValue();

            ComparisonCondition.Field field = ComparisonCondition.Field.valueOf(fieldName.toUpperCase());
            return new ComparisonCondition(field, Operator.valueOf(operator), threshold);
        } else if ("and".equals(type)) {
            List<Condition> conditions = new ArrayList<>();
            for (JsonNode cond : condNode.get("conditions")) {
                conditions.add(parseCondition(cond));
            }
            return new AndCondition(conditions);
        } else if ("or".equals(type)) {
            List<Condition> conditions = new ArrayList<>();
            for (JsonNode cond : condNode.get("conditions")) {
                conditions.add(parseCondition(cond));
            }
            return new OrCondition(conditions);
        } else if ("not".equals(type)) {
            return new NotCondition(parseCondition(condNode.get("condition")));
        }

        throw new IllegalArgumentException("Unknown condition type: " + type);
    }

    private RuleAction parseAction(JsonNode actionNode) throws Exception {
        String type = actionNode.get("type").asText();

        if ("setDecision".equals(type)) {
            String decision = actionNode.get("decision").asText();
            return new SetDecisionAction(Decision.valueOf(decision));
        } else if ("addReason".equals(type)) {
            String reason = actionNode.get("reason").asText();
            return new AddReasonAction(reason);
        } else if ("addScore".equals(type)) {
            Integer score = actionNode.get("score").asInt();
            return new AddScoreAction(score);
        }

        throw new IllegalArgumentException("Unknown action type: " + type);
    }
}
