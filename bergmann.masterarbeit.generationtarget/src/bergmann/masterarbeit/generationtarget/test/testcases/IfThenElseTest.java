package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BoolLiteral;
import bergmann.masterarbeit.generationtarget.expressions.IfThenElse;
import bergmann.masterarbeit.generationtarget.expressions.NumberLiteral;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.Unknown;

class IfThenElseTest {
	static Expression t, f, unknown;
	static State s;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		t = new BoolLiteral(true);
		f = new BoolLiteral(false);
		unknown = new Unknown<Boolean>();
		s = null;
	}
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIfThenElse() {
		// Define any a, b
		Expression a = new NumberLiteral(2.53, SI.CENTIMETER);
		Expression b = new NumberLiteral(10, SI.METER);
		
		//Possible inputs for condition
		Expression caseTrue = new IfThenElse<>(t, a, b);
		Expression caseFalse = new IfThenElse<>(f, a, b);
		Expression caseUnknown = new IfThenElse<>(unknown, a, b);
		assertEquals(a.evaluate(s), caseTrue.evaluate(s));
		assertEquals(b.evaluate(s), caseFalse.evaluate(s));
		assertEquals(Optional.empty(), caseUnknown.evaluate(s));
		
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(t,a,null);});
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(t,null,b);});
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(t,null,null);});
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(null,a,b);});
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(null,a,null);});
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(null,null,b);});
		assertThrows(IllegalArgumentException.class, ()->{new IfThenElse(null,null,null);});
	}

}
