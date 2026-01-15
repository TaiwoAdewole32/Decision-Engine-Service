package com.taiade.ruleengine.domain.condition; 
import com.taiade.ruleengine.domain.model.CaseData;
import java.math.BigDecimal;
/**
 * ComparisonCondition checks one field against one expected value using an operator.
 * Ex: age GREATER_THAN_OR_EQUALS 18, creditScore GREATER_THAN 700, hasLatePayments EQUALS false
 */
public class ComparisonCondition implements Condition { 

    private final String fieldName; //which field is being checked in CaaseData?
    private final Operator operator; //what comparison operator is being used?
    private final Object expectedValue; //what value are we comparing against?

    public ComparisonCondition(String fieldName, Operator operator, Object expectedValue) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(CaseData caseData) {
        //Get the actual value from CaseData such as age, income, creditScore, etc.
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
                //Works only for String fields
                return ((String) actualValue).contains((String) expectedValue);
            case NOT_CONTAINS:
                return !((String) actualValue).contains((String) expectedValue);
            default:
                //If the operator is unknown, throw an error
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    @Override
    public String explain(CaseData caseData) {
        //Used for logging, debugging, and explaining rule evaluations
        return "Checked " + fieldName + " " + operator + " " + expectedValue;
    }

    /**
     * Compares two numbers (actual and expected) after converting them to BigDecimal.
     * @param actual
     * @param expected
     * @return
     */
    private int compareNumbers(Object actual, Object expected) {    
        if (!(actual instanceof Number) || !(expected instanceof Number)) {
            throw new IllegalArgumentException("Both actual and expected values must be numbers for comparison.");
        }
        BigDecimal actualNum = new BigDecimal(actual.toString());
        BigDecimal expectedNum = new BigDecimal(expected.toString());
        return actualNum.compareTo(expectedNum);
    }
    /**
     * Maps fieldName string to the correct getter method in CaseData.
     * Ex: This is how "age" turns into data.getAge(), etc.
     *
     */
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