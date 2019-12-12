package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.StandaloneDataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.And;
import bergmann.masterarbeit.generationtarget.expressions.BooleanDomainValue;
import bergmann.masterarbeit.generationtarget.expressions.Implication;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Finally;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Global;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Next;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.TestMonitorDeclaration;

class LTLOperatorsTest {
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
		b = new BooleanDomainValue("B");
		expected = new BooleanDomainValue("Expected");
	}

	@Test
	void nextTest() {
		ctrl.setRealTime(true);
		ctrl.selectTable("LTL_Next");

		// a -> Next(b) == expected?
		Expression e = new Implication(a, new LTL_Next(b));

		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

	@Test
	void globalTest() {
		ctrl.setRealTime(true);
		ctrl.selectTable("LTL_Global");

		// Global(a) == expected?
		// Expression e1 = new And(a,b);
		// Expression e2 = new BoolNegation(e1);
		// Expression e3 = new LTL_Finally(e2);
		// Expression e4 = new BoolNegation(e3);
		Expression e = new LTL_Global(new And(a, b));

		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);

			// Optional<Boolean> result1 = e1.evaluate(state);
			// Optional<Boolean> result2 = e2.evaluate(state);
			// Optional<Boolean> result3 = e3.evaluate(state);
			// Optional<Boolean> result4 = e4.evaluate(state);

			// System.out.println(e3.toString() + "->" + result3);
			// System.out.println("\n"+state.toLongString());
			// System.out.println(e1.toString() + "->" + result1);
			// System.out.println(e2.toString() + "->" + result2);
			// System.out.println(e4.toString() + "->" + result4);
			// System.out.println(e.toString() + "->" + result);
			// System.out.println("expected:" + expectedResult);
			// System.out.println("------" + expectedResult.equals(result));
			// System.out.println(state.timestamp.toEpochMilli() + ": " + result + "
			// expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

	@Test
	void finallyTest() {
		ctrl.setRealTime(false);
		ctrl.selectTable("LTL_Finally");

		// Finally(a) == expected?
		Expression e = new LTL_Finally(a);

		for (State state : ctrl.stateHandler.getAllStates()) {

			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			// System.out.println(state.timestamp.toEpochMilli() + ": " + result + "
			// expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

	@Test
	void finallyTestRealTime() {
		ctrl.setRealTime(true);
		ctrl.selectTable("LTL_Finally_Realtime");

		// Finally(a) == expected?
		Expression e = new LTL_Finally(a);

		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			// System.out.println(state.timestamp.toEpochMilli() + ": " + result + "
			// expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

	@Test
	void untilTest() {
		fail("Not implemented yet");
	}

	@Test
	void weakUntilTest() {

		fail("Not implemented yet");
	}

	@Test
	void releaseTest() {
		fail("Not implemented yet");
	}

}
