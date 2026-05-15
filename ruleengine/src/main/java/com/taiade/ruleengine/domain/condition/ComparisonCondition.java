package com.taiade.ruleengine.domain.condition; 
import com.taiade.ruleengine.domain.model.CaseData;
import java.math.BigDecimal;
import java.util.function.Function;
/**
 * ComparisonCondition checks one field against one expected value using an operator.
 * Ex: age GREATER_THAN_OR_EQUALS 18, creditScore GREATER_THAN 700, hasLatePayments EQUALS false
 */
public class ComparisonCondition implements Condition { 


    public enum Field{
        APPLICANT_ID(CaseData::getApplicantId),
        AGE(CaseData::getAge), 
        INCOME(CaseData::getIncome),
        CREDIT_SCORE(CaseData::getCreditScore),
        DEBT_TO_INCOME(CaseData::getDebtToIncome),
        HAS_LATE_PAYMENTS(CaseData::getHasLatePayments),
        REQUESTED_AMOUNT(CaseData::getRequestedAmount);

        private final Function<CaseData, Object> extractor;

        Field(Function<CaseData, Object> extractor) {
            this.extractor = extractor;
        }

        public Object extract(CaseData data) {
            return extractor.apply(data);
        }
    }
<<<<<<< HEAD
    private final String fieldName; //which field is being checked in CaaseData?
    private final Operator operator; //what comparison operator is being used?
    private final Object expectedValue; //what value are we comparing against?

    public ComparisonCondition(String fieldName, Operator operator, Object expectedValue) {
        this.fieldName = fieldName;
=======
    private final Field field; //which field is being checked in CaseData?
    private final Operator operator; //what comparison operator is being used?
    private final Object expectedValue; //what value are we comparing against?

    public ComparisonCondition(Field field, Operator operator, Object expectedValue) {
        this.field = field;
>>>>>>> 5083c67 (I have to make edits to comparison condition test)
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean evaluate(CaseData caseData) {
        //Get the actual value from CaseData such as age, income, creditScore, etc.
<<<<<<< HEAD
        Object actualValue = getFieldValue(caseData, fieldName);
=======
        Object actualValue = field.extract(caseData);
>>>>>>> 5083c67 (I have to make edits to comparison condition test)
        if (actualValue == null) {
            return false;
        }

<<<<<<< HEAD
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
=======
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
>>>>>>> 5083c67 (I have to make edits to comparison condition test)
    }

    @Override
    public String explain(CaseData caseData) {
        //Used for logging, debugging, and explaining rule evaluations
<<<<<<< HEAD
        return "Checked " + fieldName + " " + operator + " " + expectedValue;
=======
        Object actualValue = field.extract(caseData);
        return "Checked " + field + " " + operator + " " + expectedValue + " (actual value: " + actualValue + ")";
>>>>>>> 5083c67 (I have to make edits to comparison condition test)
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
<<<<<<< HEAD
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
=======

    private boolean compareStrings(Object actual, Object expected) {
        if (!(actual instanceof String) || !(expected instanceof String)) {
            throw new IllegalArgumentException("Both actual and expected values must be strings for CONTAINS operator.");
        }
        return ((String) actual).contains((String) expected);
>>>>>>> 5083c67 (I have to make edits to comparison condition test)
    }

    

}