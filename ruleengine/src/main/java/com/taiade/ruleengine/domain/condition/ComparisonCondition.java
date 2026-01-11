package com.taiade.ruleengine.domain.condition; 
import com.taiade.ruleengine.domain.model.CaseData;
import java.math.BigDecimal;

public class ComparisonCondition implements Condition { 

    private final String fieldName;
    private final Operator operator;
    private final Object expectedValue;

    public ComparisonCondition(String fieldName, Operator operator, Object expectedValue) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(CaseData caseData) {
        Object actualValue = getFieldValue(caseData, fieldName);
        if (actualValue == null) {
            return false;
        }

        switch (operator) {
            case EQUALS:
                return actualValue.equals(expectedValue);
            case NOT_EQUALS:
                return !actualValue.equals(expectedValue);
            case GREATER_THAN:
                return compareNumbers(actualValue, expectedValue) > 0;
            case LESS_THAN:
                return compareNumbers(actualValue, expectedValue) < 0;
            case GREATER_THAN_OR_EQUALS:
                return compareNumbers(actualValue, expectedValue) >= 0;
            case LESS_THAN_OR_EQUALS:
                return compareNumbers(actualValue, expectedValue) <= 0;
            case CONTAINS:
                return ((String) actualValue).contains((String) expectedValue);
            case NOT_CONTAINS:
                return !((String) actualValue).contains((String) expectedValue);
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    @Override
    public String explain(CaseData caseData) {
        return "Checked " + fieldName + " " + operator + " " + expectedValue;
    }

    private int compareNumbers(Object actual, Object expected) {    
        if (!(actual instanceof Number) || !(expected instanceof Number)) {
            throw new IllegalArgumentException("Both actual and expected values must be numbers for comparison.");
        }
        BigDecimal actualNum = new BigDecimal(actual.toString());
        BigDecimal expectedNum = new BigDecimal(expected.toString());
        return actualNum.compareTo(expectedNum);
    }

    private Object getFieldValue(CaseData data, String field) {
        return switch (field.toLowerCase().trim()) {
            case "applicantid" -> data.getApplicantId();
            case "age" -> data.getAge();
            case "income" -> data.getIncome();
            case "creditscore" -> data.getCreditScore();
            case "debttoincome" -> data.getDebtToIncome();
            case "haslatepayments" -> data.getHasLatePayments();
            case "requestedamount" -> data.getRequestedAmount();
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        };
    }

    

}