package com.taiade.ruleengine.domain.condition; 
import com.taiade.ruleengine.domain.model.CaseData;
import java.math.BigDecimal;
/**
 * ComparisonCondition checks one field against one expected value using an operator.
 * Ex: age GREATER_THAN_OR_EQUALS 18, creditScore GREATER_THAN 700, hasLatePayments EQUALS false
 */
public class ComparisonCondition implements Condition { 


    public enum Field{
        APPLICANT_ID(data -> data.getApplicantId()),
        AGE(data -> data.getAge()),
        INCOME(data -> data.getIncome()),
        CREDIT_SCORE(data -> data.getCreditScore()),
        DEBT_TO_INCOME(data -> data.getDebtToIncome()),
        HAS_LATE_PAYMENTS(data -> data.getHasLatePayments()),
        REQUESTED_AMOUNT(data -> data.getRequestedAmount()),
        EMPLOYED(data -> data.isEmployed());

        private final Extractor extractor;

        Field(Extractor extractor) {
            this.extractor = extractor;
        }

        public Object extract(CaseData data) {
            return extractor.apply(data);
        }

        @FunctionalInterface
        interface Extractor {
            Object apply(CaseData data);
        }
    }
    private final Field field; //which field is being checked in CaseData?
    private final Operator operator; //what comparison operator is being used?
    private final Object expectedValue; //what value are we comparing against?

    public ComparisonCondition(Field field, Operator operator, Object expectedValue) {
        this.field = field;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(CaseData caseData) {
        //Get the actual value from CaseData such as age, income, creditScore, etc.
        Object actualValue = field.extract(caseData);
        if (actualValue == null) {
            return false;
        }

        return switch (operator) {
            case EQUALS -> actualValue.equals(expectedValue);
            case NOT_EQUALS -> !actualValue.equals(expectedValue);
            case GREATER_THAN -> compareNumbers(actualValue, expectedValue) > 0;
            case LESS_THAN -> compareNumbers(actualValue, expectedValue) < 0;
            case GREATER_THAN_OR_EQUALS -> compareNumbers(actualValue, expectedValue) >= 0;
            case LESS_THAN_OR_EQUALS -> compareNumbers(actualValue, expectedValue) <= 0;
            case CONTAINS -> compareStrings(actualValue, expectedValue);
            case NOT_CONTAINS -> !compareStrings(actualValue, expectedValue);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    @Override
    public String explain(CaseData caseData) {
        //Used for logging, debugging, and explaining rule evaluations
        Object actualValue = field.extract(caseData);
        return "Checked " + field + " " + operator + " " + expectedValue + " (actual value: " + actualValue + ")";
    }

    /**
     * Compares two numbers (actual and expected) after converting them to BigDecimal.
     * @param actual
     * @param expected
     * @return
     */
    private int compareNumbers(Object actual, Object expected) {    
        if (!(actual instanceof Number) || !(expected instanceof Number)) {
           throw new IllegalStateException("Field " + field + "is not numeric");
        }
        BigDecimal actualNum = new BigDecimal(actual.toString());
        BigDecimal expectedNum = new BigDecimal(expected.toString());
        return actualNum.compareTo(expectedNum);
    }

    private boolean compareStrings(Object actual, Object expected) {
        if (!(actual instanceof String) || !(expected instanceof String)) {
            throw new IllegalStateException("Field " + field + " is not a String");
        }
        return ((String) actual).contains((String) expected);
    }

    

}