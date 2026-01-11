package com.taiade.ruleengine;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.taiade.ruleengine.domain.condition.*;
import com.taiade.ruleengine.domain.model.CaseData;

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

	

}
