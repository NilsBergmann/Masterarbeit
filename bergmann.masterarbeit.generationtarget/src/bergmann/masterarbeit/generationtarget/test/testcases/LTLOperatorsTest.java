package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.And;
import bergmann.masterarbeit.generationtarget.expressions.BoolDatabaseAccess;
import bergmann.masterarbeit.generationtarget.expressions.Implication;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Finally;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Global;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Next;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class LTLOperatorsTest {
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
	void nextTest() {
		ctrl.isRealTime = false;
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.registerBooleanDBColumn("B");
		b = new BoolDatabaseAccess("B");
		ctrl.registerBooleanDBColumn("Expected");
		expected = new BoolDatabaseAccess("Expected");
		ctrl.selectTable("LTL_Next");
		
		// a -> Next(b) == expected?
		Expression e = new Implication(a, new LTL_Next(b));
		
		for (State state : ctrl.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}
	
	@Test
	void globalTest() {
		ctrl.isRealTime = false;
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.registerBooleanDBColumn("B");
		b = new BoolDatabaseAccess("B");
		ctrl.registerBooleanDBColumn("Expected");
		expected = new BoolDatabaseAccess("Expected");
		ctrl.selectTable("LTL_Global");
		
		// Global(a) == expected?
		Expression e = new LTL_Global(new And(a,b));
		
		for (State state : ctrl.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			System.out.println(state.toLongString() + " -> " + result + " | "+expectedResult);
			//System.out.println(state.timestamp.toEpochMilli() + ": " + result + " expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}
	
	@Test 
	void finallyTest() {
		ctrl.isRealTime = false;
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.registerBooleanDBColumn("B");
		b = new BoolDatabaseAccess("B");
		ctrl.registerBooleanDBColumn("Expected");
		expected = new BoolDatabaseAccess("Expected");
		ctrl.selectTable("LTL_Finally");
		
		// Finally(a) == expected?
		Expression e = new LTL_Finally(a);
		
		for (State state : ctrl.getAllStates()) {

			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			//System.out.println(state.timestamp.toEpochMilli() + ": " + result + " expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}
	
	@Test 
	void finallyTestRealTime() {
		ctrl.isRealTime = true;
		ctrl.registerBooleanDBColumn("A");
		a = new BoolDatabaseAccess("A");
		ctrl.registerBooleanDBColumn("B");
		b = new BoolDatabaseAccess("B");
		ctrl.registerBooleanDBColumn("Expected");
		expected = new BoolDatabaseAccess("Expected");
		ctrl.selectTable("LTL_Finally_Realtime");
		
		// Finally(a) == expected?
		Expression e = new LTL_Finally(a);
		
		for (State state : ctrl.getAllStates()) {
			Optional<Boolean> result = e.evaluate(state);
			Optional<Boolean> expectedResult = expected.evaluate(state);
			//System.out.println(state.timestamp.toEpochMilli() + ": " + result + " expected " + expectedResult);
			assertEquals(expectedResult, result, "Timestamp " + state.timestamp.toEpochMilli());
		}
	}

}
