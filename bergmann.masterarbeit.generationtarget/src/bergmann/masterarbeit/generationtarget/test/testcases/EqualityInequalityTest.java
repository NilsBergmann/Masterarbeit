package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BoolLiteral;
import bergmann.masterarbeit.generationtarget.expressions.Equals;
import bergmann.masterarbeit.generationtarget.expressions.NotEquals;
import bergmann.masterarbeit.generationtarget.expressions.StringLiteral;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.Unknown;

class EqualityInequalityTest {
	static Expression unknown;
	static Expression<Boolean> True, False;
	static Expression<String> stringA, stringA2, stringB, emptyString;
	static State s;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		unknown = new Unknown();
		
		True = new BoolLiteral(true);
		False = new BoolLiteral(false);
	
		stringA = new StringLiteral("A");
		stringA2 = new StringLiteral("A");
		
		stringB = new StringLiteral("B");
		
		emptyString = new StringLiteral("");
		s = null;
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void unknownEquality() {
		Expression eq = new Equals(True, unknown);
		Optional res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		eq = new Equals(unknown, True);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		// Other dataType
		eq = new Equals(stringA, unknown);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		// Empty string
		eq = new Equals(emptyString, unknown);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		// Unknown and unknown
		eq = new Equals(unknown, unknown);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
	}
	
	@Test
	void unknownInequality() {
		Expression eq = new NotEquals(True, unknown);
		Optional res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		eq = new NotEquals(unknown, True);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		// Other dataType
		eq = new NotEquals(stringA, unknown);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		// Empty string
		eq = new NotEquals(emptyString, unknown);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
		
		// Unknown and unknown
		eq = new NotEquals(unknown, unknown);
		res = eq.evaluate(s);
		assertEquals(Optional.empty(), res);
	}
	
	@Test
	void nullTest() {
		assertThrows(IllegalArgumentException.class, ()->{ new Equals<>(True, null); });
		assertThrows(IllegalArgumentException.class, ()->{ new Equals<>(null, True); });
		assertThrows(IllegalArgumentException.class, ()->{ new Equals<>(null, null); });
		
		assertThrows(IllegalArgumentException.class, ()->{ new Equals<>(unknown, null); });
		assertThrows(IllegalArgumentException.class, ()->{ new Equals<>(null, unknown); });
		
	}



}
