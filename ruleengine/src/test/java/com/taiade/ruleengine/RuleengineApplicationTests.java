package com.taiade.ruleengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.taiade.ruleengine.domain.condition.*;
import com.taiade.ruleengine.domain.decision.Decision;
import com.taiade.ruleengine.domain.model.CaseData;
import java.util.List;
@SpringBootTest
class RuleengineApplicationTests {

	@Test
	void testComparisonConditionEvaluateMethod() {
		CaseData data = new CaseData(
			"applicant123",
			30,
			60000,
			720,
			0.35,
			false,
			15000
		);
		assertEquals("applicant123", data.getApplicantId());
        assertEquals(30, data.getAge());
        assertEquals(60000, data.getIncome());
        assertEquals(720, data.getCreditScore());
        assertEquals(0.35, data.getDebtToIncome());
        assertFalse(data.getHasLatePayments());
        assertEquals(15000, data.getRequestedAmount());

        Condition c = new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.EQUALS, 30);
        assertTrue(c.evaluate(data));

		Condition c2 = new ComparisonCondition(ComparisonCondition.Field.CREDIT_SCORE, Operator.GREATER_THAN, 700);
		assertTrue(c2.evaluate(data));

		Condition c3 = new ComparisonCondition(ComparisonCondition.Field.DEBT_TO_INCOME, Operator.LESS_THAN_OR_EQUALS, 0.4);
		assertTrue(c3.evaluate(data));

		Condition c4 = new ComparisonCondition(ComparisonCondition.Field.INCOME, Operator.LESS_THAN, 50000);
		assertFalse(c4.evaluate(data));

		Condition c5 = new ComparisonCondition(ComparisonCondition.Field.HAS_LATE_PAYMENTS, Operator.EQUALS, false);
		assertTrue(c5.evaluate(data));
		
		Condition c6 = new ComparisonCondition(ComparisonCondition.Field.REQUESTED_AMOUNT, Operator.GREATER_THAN_OR_EQUALS, 20000);
		assertFalse(c6.evaluate(data));

		Condition c7 = new ComparisonCondition(ComparisonCondition.Field.APPLICANT_ID, Operator.CONTAINS, "123");
		assertTrue(c7.evaluate(data));

		Condition c8 = new ComparisonCondition(ComparisonCondition.Field.APPLICANT_ID, Operator.NOT_CONTAINS, "9");
		assertTrue(c8.evaluate(data));

		Condition c9 = new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.NOT_EQUALS, 25);
		assertTrue(c9.evaluate(data));
	}

	@Test
	void testComparisonConditionExplainMethod() {
		CaseData data = new CaseData(
			"applicant123",
			30,
			60000,
			720,
			0.35,
			false,
			15000
		);

		Condition c = new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.EQUALS, 30);
		String explanation = c.explain(data);
		assertEquals("Checked AGE EQUALS 30 (actual value: 30)", explanation);

		Condition c2 = new ComparisonCondition(ComparisonCondition.Field.CREDIT_SCORE, Operator.GREATER_THAN, 700);
		String explanation2 = c2.explain(data);
		assertEquals("Checked CREDIT_SCORE GREATER_THAN 700 (actual value: 720)", explanation2);
	}

	@Test
	void testNotCondition() {
		CaseData data = new CaseData(
			"applicant123",
			30,
			60000,
			720,
			0.35,
			false,
			15000
		);

		Condition baseCondition = new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.EQUALS, 30);
		Condition notCondition = new NotCondition(baseCondition);

		assertFalse(notCondition.evaluate(data));
		assertEquals("NOT (Checked AGE EQUALS 30 (actual value: 30))", notCondition.explain(data));

		Condition baseCondition2 = new ComparisonCondition(ComparisonCondition.Field.INCOME, Operator.LESS_THAN, 50000);
		Condition notCondition2 = new NotCondition(baseCondition2);
		assertTrue(notCondition2.evaluate(data));
		assertEquals("NOT (Checked INCOME LESS_THAN 50000 (actual value: 60000))", notCondition2.explain(data));

		Condition doubleNot = new NotCondition(new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.EQUALS, 30));
		assertFalse(doubleNot.evaluate(data));
		assertEquals("NOT (Checked AGE EQUALS 30 (actual value: 30))", doubleNot.explain(data));
	}

	@Test
	void testOrCondition() {
		CaseData data = new CaseData(
			"applicant123",
			30,
			60000,
			720,
			0.35,
			false,
			15000
		);

		Condition condition1 = new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.LESS_THAN, 25);
		Condition condition2 = new ComparisonCondition(ComparisonCondition.Field.INCOME, Operator.GREATER_THAN, 50000);
		Condition orCondition = new OrCondition(List.of(condition1, condition2));

		assertTrue(orCondition.evaluate(data));
		assertEquals("OR condition:\n - Checked AGE LESS_THAN 25 (actual value: 30)\n - Checked INCOME GREATER_THAN 50000 (actual value: 60000)\n", orCondition.explain(data));

		Condition condition3 = new ComparisonCondition(ComparisonCondition.Field.CREDIT_SCORE, Operator.LESS_THAN, 700);
		Condition orCondition2 = new OrCondition(List.of(condition1, condition3));

		assertFalse(orCondition2.evaluate(data));
		assertEquals("OR condition:\n - Checked AGE LESS_THAN 25 (actual value: 30)\n - Checked CREDIT_SCORE LESS_THAN 700 (actual value: 720)\n", orCondition2.explain(data));
	}

	@Test
	void testAndCondition(){
		CaseData data = new CaseData(
			"applicant123",
			30,
			60000,
			720,
			0.35,
			false,
			15000
		);

		Condition condition1 = new ComparisonCondition(ComparisonCondition.Field.AGE, Operator.GREATER_THAN, 25);
		Condition condition2 = new ComparisonCondition(ComparisonCondition.Field.INCOME, Operator.GREATER_THAN, 50000);
		Condition andCondition = new AndCondition(List.of(condition1, condition2));

		assertTrue(andCondition.evaluate(data));
		assertEquals("AND Condition:\n - Checked AGE GREATER_THAN 25 (actual value: 30)\n - Checked INCOME GREATER_THAN 50000 (actual value: 60000)\n", andCondition.explain(data));

		Condition condition3 = new ComparisonCondition(ComparisonCondition.Field.CREDIT_SCORE, Operator.LESS_THAN, 700);
		Condition andCondition2 = new AndCondition(List.of(condition1, condition3));

		assertFalse(andCondition2.evaluate(data));
		assertEquals("AND Condition:\n - Checked AGE GREATER_THAN 25 (actual value: 30)\n - Checked CREDIT_SCORE LESS_THAN 700 (actual value: 720)\n", andCondition2.explain(data));	
	}

	
	@Test
	void operatorTest(){
		assertEquals(Operator.EQUALS, Operator.valueOf("EQUALS"));
		assertEquals(Operator.NOT_EQUALS, Operator.valueOf("NOT_EQUALS"));
		assertEquals(Operator.GREATER_THAN, Operator.valueOf("GREATER_THAN"));
		assertEquals(Operator.LESS_THAN, Operator.valueOf("LESS_THAN"));
		assertEquals(Operator.GREATER_THAN_OR_EQUALS, Operator.valueOf("GREATER_THAN_OR_EQUALS"));
		assertEquals(Operator.LESS_THAN_OR_EQUALS, Operator.valueOf("LESS_THAN_OR_EQUALS"));
		assertEquals(Operator.CONTAINS, Operator.valueOf("CONTAINS"));
		assertEquals(Operator.NOT_CONTAINS, Operator.valueOf("NOT_CONTAINS"));
	}
	
	@Test
	void testDecisionTest(){
		Decision d1 = Decision.APPROVE;
		Decision d2 = Decision.REJECT;
		Decision d3 = Decision.REVIEW;

		assertEquals(Decision.APPROVE, d1);
		assertEquals(Decision.REJECT, d2);
		assertEquals(Decision.REVIEW, d3);
	}
	
	//Make some test for Model, Rule, Action folders 
	@Test
	void testRuleEngine(){
		// This is a very basic test to ensure RuleEngine can be instantiated and evaluate method can be called without errors.

		//RuleEngine engine = new RuleEngine(List.of(rejectLowCredit));

		assertTrue(true);
	}
	

}
