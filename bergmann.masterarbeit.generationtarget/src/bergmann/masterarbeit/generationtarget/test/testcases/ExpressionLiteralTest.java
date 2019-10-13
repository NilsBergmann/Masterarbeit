package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BoolLiteral;
import bergmann.masterarbeit.generationtarget.expressions.NumberLiteral;
import bergmann.masterarbeit.generationtarget.expressions.StringLiteral;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class ExpressionLiteralTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void boolLiteralTest() {
		State s = null;
		Expression t = new BoolLiteral(true);
		assertEquals(Optional.of(true), t.evaluate(s));

		Expression f = new BoolLiteral(false);
		assertEquals(Optional.of(false), f.evaluate(s));
	}
	
	@Test
	void numberLiteralTest() {
		State s = null;
		
		double value = 2.0;
		Unit<Length> unit = SI.KILOMETER;
		
		Amount expected = Amount.valueOf(value, unit);
		Expression e = new NumberLiteral(value, unit);
		assertEquals(Optional.of(expected), e.evaluate(s));
	}
	
	@Test
	void stringLiteralTest() {
		State s = null;
		
		String value = "Teststring 123 123 123";	
		Expression e = new StringLiteral(value);
		assertEquals(Optional.of(value), e.evaluate(s));
		
		Expression e2 = new StringLiteral(null);
		assertEquals(Optional.empty(), e2.evaluate(s));
	}

}
