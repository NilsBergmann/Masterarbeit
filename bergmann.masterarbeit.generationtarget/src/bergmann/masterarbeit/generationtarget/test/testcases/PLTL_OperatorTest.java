package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BoolDatabaseAccess;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Historically;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Once;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Yesterday;
import bergmann.masterarbeit.generationtarget.expressions.PLTL_Z;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class PLTL_OperatorTest {
	static DataController ctrl;
	static Expression a, b, expected;
	
	@BeforeAll
	public static void init() {

	}
	
	@BeforeEach
	void setUp() throws Exception {
		ctrl = new DataController(false);
		ctrl.connectToDatabase("Testcases.db");
	}

	@Test
	void onceTest() {
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.registerBooleanDBColumn("Expected");
		expected = new BoolDatabaseAccess("Expected");
		ctrl.selectTable("PLTL_Once");
		
		Expression e = new PLTL_Once(a);
		
		for (State state : ctrl.getAllStates()) {

			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			System.out.println(state.timestamp.toEpochMilli() + ": " + result + " expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}
	
	@Test
	void HistoricallyTest() {
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.registerBooleanDBColumn("Expected");
		expected = new BoolDatabaseAccess("Expected");
		ctrl.selectTable("PLTL_Historically");
		
		Expression e = new PLTL_Historically(a);
		
		for (State state : ctrl.getAllStates()) {
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
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.selectTable("PLTL_Yesterday");
		
		Expression sub = a;
		Expression e = new PLTL_Yesterday(sub);
		
		for (State state : ctrl.getAllStates()) {
			Optional result = e.evaluate(state);
			State previous = ctrl.getPreviousState(state);
			if(previous == null)
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
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.selectTable("PLTL_Yesterday");
		
		Expression sub = a;
		Expression e = new PLTL_Z(sub);
		
		for (State state : ctrl.getAllStates()) {
			Optional result = e.evaluate(state);
			State previous = ctrl.getPreviousState(state);
			if(previous == null)
				assertEquals(Optional.of(true), result);
			else {
				Optional previousValue = sub.evaluate(previous);
				assertEquals(previousValue, result);
			}
		}
	}
	
	@Test
	void TriggerTest() {
		fail("Not implemented yet");
	}
}
