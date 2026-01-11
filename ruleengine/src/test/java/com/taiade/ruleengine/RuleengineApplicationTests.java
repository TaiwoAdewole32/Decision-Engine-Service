package com.taiade.ruleengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.taiade.ruleengine.domain.condition.*;
import com.taiade.ruleengine.domain.model.CaseData;
import java.util.List;
@SpringBootTest
class RuleengineApplicationTests {

	@Test
	void testEvaluateMethod() {
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

        Condition c = new ComparisonCondition("age", Operator.EQUALS, 30);
        assertTrue(c.evaluate(data));

		Condition c2 = new ComparisonCondition("creditScore", Operator.GREATER_THAN, 700);
		assertTrue(c2.evaluate(data));

		Condition c3 = new ComparisonCondition("debtToIncome", Operator.LESS_THAN_OR_EQUALS, 0.4);
		assertTrue(c3.evaluate(data));

		Condition c4 = new ComparisonCondition("income", Operator.LESS_THAN, 50000);
		assertFalse(c4.evaluate(data));

		Condition c5 = new ComparisonCondition("hasLatePayments", Operator.EQUALS, false);
		assertTrue(c5.evaluate(data));
		
		Condition c6 = new ComparisonCondition("requestedAmount", Operator.GREATER_THAN_OR_EQUALS, 20000);
		assertFalse(c6.evaluate(data));

		Condition c7 = new ComparisonCondition("applicantId", Operator.CONTAINS, "123");
		assertTrue(c7.evaluate(data));

		Condition c8 = new ComparisonCondition("applicantId", Operator.NOT_CONTAINS, "9");
		assertTrue(c8.evaluate(data));

		Condition c9 = new ComparisonCondition("age", Operator.NOT_EQUALS, 25);
		assertTrue(c9.evaluate(data));
	}

	@Test
	void testExplainMethod() {
		CaseData data = new CaseData(
			"applicant123",
			30,
			60000,
			720,
			0.35,
			false,
			15000
		);

		Condition c = new ComparisonCondition("age", Operator.EQUALS, 30);
		String explanation = c.explain(data);
		assertEquals("Checked age EQUALS 30", explanation);

		Condition c2 = new ComparisonCondition("creditScore", Operator.GREATER_THAN, 700);
		String explanation2 = c2.explain(data);
		assertEquals("Checked creditScore GREATER_THAN 700", explanation2);
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

		Condition baseCondition = new ComparisonCondition("age", Operator.EQUALS, 30);
		Condition notCondition = new NotCondition(baseCondition);

		assertFalse(notCondition.evaluate(data));
		assertEquals("NOT (Checked age EQUALS 30)", notCondition.explain(data));

		Condition baseCondition2 = new ComparisonCondition("income", Operator.LESS_THAN, 50000);
		Condition notCondition2 = new NotCondition(baseCondition2);
		assertTrue(notCondition2.evaluate(data));
		assertEquals("NOT (Checked income LESS_THAN 50000)", notCondition2.explain(data));

		Condition doubleNot = new NotCondition(new ComparisonCondition("age", Operator.EQUALS, 30));
		assertFalse(doubleNot.evaluate(data));
		assertEquals("NOT (Checked age EQUALS 30)", doubleNot.explain(data));
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

		Condition condition1 = new ComparisonCondition("age", Operator.LESS_THAN, 25);
		Condition condition2 = new ComparisonCondition("income", Operator.GREATER_THAN, 50000);
		Condition orCondition = new OrCondition(List.of(condition1, condition2));

		assertTrue(orCondition.evaluate(data));
		assertEquals("OR condition:\n - Checked age LESS_THAN 25\n - Checked income GREATER_THAN 50000\n", orCondition.explain(data));

		Condition condition3 = new ComparisonCondition("creditScore", Operator.LESS_THAN, 700);
		Condition orCondition2 = new OrCondition(List.of(condition1, condition3));

		assertFalse(orCondition2.evaluate(data));
		assertEquals("OR condition:\n - Checked age LESS_THAN 25\n - Checked creditScore LESS_THAN 700\n", orCondition2.explain(data));
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

		Condition condition1 = new ComparisonCondition("age", Operator.GREATER_THAN, 25);
		Condition condition2 = new ComparisonCondition("income", Operator.GREATER_THAN, 50000);
		Condition andCondition = new AndCondition(List.of(condition1, condition2));

		assertTrue(andCondition.evaluate(data));
		assertEquals("AND Condition:\n - Checked age GREATER_THAN 25\n - Checked income GREATER_THAN 50000\n", andCondition.explain(data));

		Condition condition3 = new ComparisonCondition("creditScore", Operator.LESS_THAN, 700);
		Condition andCondition2 = new AndCondition(List.of(condition1, condition3));

		assertFalse(andCondition2.evaluate(data));
		assertEquals("AND Condition:\n - Checked age GREATER_THAN 25\n - Checked creditScore LESS_THAN 700\n", andCondition2.explain(data));	
	}

	

}
