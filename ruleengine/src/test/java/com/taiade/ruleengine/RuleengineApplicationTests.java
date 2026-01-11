package com.taiade.ruleengine;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.taiade.ruleengine.domain.condition.*;
import com.taiade.ruleengine.domain.model.CaseData;

@SpringBootTest
class RuleengineApplicationTests {

	@Test
	void contextLoads() {
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

		
	}

}
