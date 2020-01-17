package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.StandaloneDataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BooleanDomainValue;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Historically;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Once;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Yesterday;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Z;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.TestMonitorDeclaration;

class PLTL_OperatorTest {
	static StandaloneDataController ctrl;
	static Expression a, b, expected;

	@BeforeAll
	public static void init() {

	}

	@BeforeEach
	void setUp() throws Exception {
		ctrl = new StandaloneDataController(false);
		TestMonitorDeclaration decl = new TestMonitorDeclaration();
		decl.addDomainBoolean("A");
		decl.addDomainBoolean("B");
		decl.addDomainBoolean("Expected");
		ctrl.registerRequiredData(decl);
		ctrl.connectToDatabase("Testcases.db");

		a = new BooleanDomainValue("A");
		expected = new BooleanDomainValue("Expected");
	}

	@Test
	void onceTest() {
		ctrl.selectTable("PLTL_Once");

		Expression e = new PLTL_Once(a);

		for (State state : ctrl.stateHandler.getAllStates()) {

			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			System.out.println(state.timestamp.toEpochMilli() + ": " + result + " expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

	@Test
	void HistoricallyTest() {
		ctrl.selectTable("PLTL_Historically");

		Expression e = new PLTL_Historically(a);

		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			System.out.println(state.timestamp.toEpochMilli() + ": " + result + " expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

	@Test
	void sinceTest() {
		fail("Not implemented yet");
	}

	@Test
	void YesterdayTest() {
		ctrl.selectTable("PLTL_Yesterday");

		Expression sub = a;
		Expression e = new PLTL_Yesterday(sub);

		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional result = e.evaluate(state);
			State previous = ctrl.stateHandler.getPreviousState(state);
			if (previous == null)
				assertEquals(Optional.of(false), result);
			else {
				Optional previousValue = sub.evaluate(previous);
				assertEquals(previousValue, result);
			}
		}
	}

	@Test
	void ZTest() {
		// Same as Y, except its true at state 0
		ctrl.selectTable("PLTL_Yesterday");

		Expression sub = a;
		Expression e = new PLTL_Z(sub);

		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional result = e.evaluate(state);
			State previous = ctrl.stateHandler.getPreviousState(state);
			if (previous == null)
				assertEquals(Optional.of(true), result);
			else {
				Optional previousValue = sub.evaluate(previous);
				assertEquals(previousValue, result);
			}
		}
	}

	@Test
	void TriggerTest() {
		// TODO: Implement test cases
		fail("Not implemented yet");
	}
}
