package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.dataaccess.StateListHandler;
import bergmann.masterarbeit.generationtarget.expressions.BoolDatabaseAccess;
import bergmann.masterarbeit.generationtarget.expressions.BoolLiteral;
import bergmann.masterarbeit.generationtarget.expressions.NumberDatabaseAccess;
import bergmann.masterarbeit.generationtarget.expressions.StringDatabaseAccess;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.Assertion;
import bergmann.masterarbeit.generationtarget.utils.UserVariable;

class StateInformationTest {
	static StateListHandler states;
	
	@BeforeAll
	public static void init() {
		states = new StateListHandler();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void test() {
		for(int i = 0; i < 100; i++) {
			State current = new State(i);
			Amount test = Amount.valueOf(i, SI.METER);
			current.storeDBValue("Test", Optional.of(test));
			states.add(current);
		}
		assertEquals(100, states.getAllStates().size());
		
		Expression getTestExpr= new NumberDatabaseAccess("Test");
		Expression getWrongType = new StringDatabaseAccess("Test");
		Expression getUndefinedValueExpr = new BoolDatabaseAccess("Undefined");
		
		UserVariable userVar = new UserVariable<>("UserVariableTest", getTestExpr);
		Assertion assertion = new Assertion("AssertionTest", getTestExpr);

		for (State s : states.getAllStates()) {
			Optional<Amount> result = getTestExpr.evaluate(s);
			assertTrue(result.isPresent());
			assertEquals(SI.METER, result.get().getUnit());
			
			// Wrong type returns Optional.empty()
			assertFalse(getWrongType.evaluate(s).isPresent());
			
			// Undefined values return Optional.empty()
			assertFalse(getUndefinedValueExpr.evaluate(s).isPresent());

			// After evaluating a UserVariable once, there should be a value stored for it in the state
			assertEquals(0, s.getStoredUserVariableIDs().size());
			assertFalse(s.getStoredUserVariableIDs().contains("UserVariableTest"));
			userVar.evaluate(s);
			assertEquals(1, s.getStoredUserVariableIDs().size());	
			assertTrue(s.getStoredUserVariableIDs().contains("UserVariableTest"));
			
			// Same for assertions
			assertEquals(0, s.getStoredAssertionIDs().size());
			assertFalse(s.getStoredAssertionIDs().contains("AssertionTest"));
			assertion.evaluate(s);
			assertEquals(1, s.getStoredAssertionIDs().size());	
			assertTrue(s.getStoredAssertionIDs().contains("AssertionTest"));
		}
		
	}

}
