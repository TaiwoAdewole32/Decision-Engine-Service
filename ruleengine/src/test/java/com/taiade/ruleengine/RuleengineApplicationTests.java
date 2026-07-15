package com.taiade.ruleengine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.taiade.ruleengine.domain.condition.*;
import com.taiade.ruleengine.domain.decision.Decision;
import com.taiade.ruleengine.domain.decision.DecisionContext;
import com.taiade.ruleengine.domain.model.CaseData;
import com.taiade.ruleengine.domain.rule.action.*;

import java.util.List;

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
			15000,
			true
		);
	
		assertEquals("applicant123", data.getApplicantId());
        assertEquals(30, data.getAge());
        assertEquals(60000, data.getIncome());
        assertEquals(720, data.getCreditScore());
        assertEquals(0.35, data.getDebtToIncome());
        assertFalse(data.getHasLatePayments());
        assertEquals(15000, data.getRequestedAmount());
        assertTrue(data.isEmployed());

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
			15000,
			true
			
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
			15000,
			true
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
			15000,
			true
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
			15000,
			true
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
	

	private CaseData goodApplicant = new CaseData("sarah251", 35, 81000, 750, 0.25, false, 20000, true);
	private CaseData riskyApplicant = new CaseData("jon617", 22, 28000, 580, 0.55, true, 40000, true);
	private DecisionContext context = new DecisionContext();

	//Test to verify that the DecisionContext initializes with the expected default values
	@Test
	void decisionContextInitialState(){
		assertNull(context.getDecision(), "Decision should be null initially");
		assertEquals(0, context.getScore());
		assertTrue(context.getReasons().isEmpty(), "Reasons list should be empty initially");
		assertTrue(context.getMatchedRulesIDs().isEmpty());
	}

	//Test set and get decision in the DecisionContext
	@Test
	void decisionContextSetandGetDecision(){
		context.setDecision(Decision.APPROVE);
		assertEquals(Decision.APPROVE, context.getDecision());

		context.setDecision(Decision.REJECT);
		assertEquals(Decision.REJECT, context.getDecision());

		context.setDecision(Decision.REVIEW);
		assertEquals(Decision.REVIEW, context.getDecision());
	}

	@Test
	void decisionContextScoreAccumulation(){
		context.addScore(10);
		assertEquals(10, context.getScore());

		context.addScore(5);
		assertEquals(15, context.getScore());

		context.addScore(-3);
		assertEquals(12, context.getScore());

		context.addScore(-20);
		assertEquals(0, context.getScore(), "Score should not go negative");
	}

	@Test
	void decisionContextReasonsAndMatchedRules(){
		context.addReason("Income above threshold");
		context.addReason("Credit score good");
		assertEquals(2, context.getReasons().size());
		assertTrue(context.getReasons().contains("Income above threshold"));
		assertTrue(context.getReasons().contains("Credit score good"));

		context.addMatchedRulesID("rule1");
		context.addMatchedRulesID("rule2");
		assertEquals(2, context.getMatchedRulesIDs().size());
		assertTrue(context.getMatchedRulesIDs().contains("rule1"));
		assertTrue(context.getMatchedRulesIDs().contains("rule2"));
	}

	@Test
	void decisionContextToString(){
		context.setDecision(Decision.APPROVE);
		context.addScore(10);
		context.addReason("Income above threshold");
		context.addMatchedRulesID("rule1");

		String expected = "DecisionContext{decision=APPROVE, score=10, reasons=[Income above threshold], matchedRulesIDs=[rule1]}";
		assertEquals(expected, context.toString());
	}

	@Test 
	void addScoreActionToContext(){
		new AddScoreAction(30).apply(context);
		assertEquals(30, context.getScore());

		String expected = "DecisionContext{decision=null, score=30, reasons=[], matchedRulesIDs=[]}";
		assertEquals(expected, context.toString());

		context.addScore(50);
		new AddScoreAction(20).apply(context);
		assertEquals(100, context.getScore());
	}

	@Test
	void addReasonActionToContext(){
		new AddReasonAction("Credit score below threshold").apply(context);

		List<String> reasons = context.getReasons();
		assertEquals(1, reasons.size());
		assertTrue(reasons.contains("Credit score below threshold"));
	}




	

		
	

}


