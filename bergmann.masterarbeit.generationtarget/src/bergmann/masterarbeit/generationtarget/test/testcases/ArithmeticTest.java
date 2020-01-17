package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.mathematics.number.Real;
import org.jscience.physics.amount.Amount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.Addition;
import bergmann.masterarbeit.generationtarget.expressions.Division;
import bergmann.masterarbeit.generationtarget.expressions.IfThenElse;
import bergmann.masterarbeit.generationtarget.expressions.Multiplication;
import bergmann.masterarbeit.generationtarget.expressions.NumberAbsolute;
import bergmann.masterarbeit.generationtarget.expressions.NumberLiteral;
import bergmann.masterarbeit.generationtarget.expressions.NumberNegation;
import bergmann.masterarbeit.generationtarget.expressions.NumberRoot;
import bergmann.masterarbeit.generationtarget.expressions.Subtraction;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.Unknown;

class ArithmeticTest {
	static Expression<Amount> leftExpr, rightExpr, unknown;
	static Amount leftAmount, rightAmount;
	static State s;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		double leftValue = 2;
		double rightValue = 5.0;
		
		Unit leftUnit = SI.KILOMETER;
		Unit rightUnit = SI.CENTIMETER;
		
		leftExpr = new NumberLiteral(leftValue, leftUnit);
		rightExpr = new NumberLiteral(rightValue, rightUnit);
		unknown = new Unknown<Amount>();
		
		leftAmount = Amount.valueOf(leftValue, leftUnit);
		rightAmount = Amount.valueOf(rightValue, rightUnit);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void AdditionTest() {
		Expression expr = new Addition(leftExpr, rightExpr);
		Amount expected = leftAmount.plus(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
		
		//Unknown values
		Expression leftUnknown = new Addition(unknown, rightExpr);
		Expression rightUnknown = new Addition(leftExpr, unknown);
		Expression bothUnknown = new Addition(unknown, unknown);
		assertEquals(Optional.empty(), leftUnknown.evaluate(s));
		assertEquals(Optional.empty(), rightUnknown.evaluate(s));
		assertEquals(Optional.empty(), bothUnknown.evaluate(s));
		
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new Addition(leftExpr,null);});
		assertThrows(IllegalArgumentException.class, ()->{new Addition(null,rightExpr);});
		assertThrows(IllegalArgumentException.class, ()->{new Addition(null,null);});
	}
	
	@Test
	void SubtractionTest() {
		Expression expr = new Subtraction(leftExpr, rightExpr);
		Amount expected = leftAmount.minus(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
		
		//Unknown values
		Expression leftUnknown = new Subtraction(unknown, rightExpr);
		Expression rightUnknown = new Subtraction(leftExpr, unknown);
		Expression bothUnknown = new Subtraction(unknown, unknown);
		assertEquals(Optional.empty(), leftUnknown.evaluate(s));
		assertEquals(Optional.empty(), rightUnknown.evaluate(s));
		assertEquals(Optional.empty(), bothUnknown.evaluate(s));
		
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new Subtraction(leftExpr,null);});
		assertThrows(IllegalArgumentException.class, ()->{new Subtraction(null,rightExpr);});
		assertThrows(IllegalArgumentException.class, ()->{new Subtraction(null,null);});
	}
	
	@Test
	void MultiplicationTest() {
		Expression expr = new Multiplication(leftExpr, rightExpr);
		Amount expected = leftAmount.times(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
		
		//Unknown values
		Expression leftUnknown = new Multiplication(unknown, rightExpr);
		Expression rightUnknown = new Multiplication(leftExpr, unknown);
		Expression bothUnknown = new Multiplication(unknown, unknown);
		assertEquals(Optional.empty(), leftUnknown.evaluate(s));
		assertEquals(Optional.empty(), rightUnknown.evaluate(s));
		assertEquals(Optional.empty(), bothUnknown.evaluate(s));
		
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new Multiplication(leftExpr,null);});
		assertThrows(IllegalArgumentException.class, ()->{new Multiplication(null,rightExpr);});
		assertThrows(IllegalArgumentException.class, ()->{new Multiplication(null,null);});
	}
	
	@Test
	void DivisionTest() {
		Expression expr = new Division(leftExpr, rightExpr);
		Amount expected = leftAmount.divide(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
		
		//Unknown values
		Expression leftUnknown = new Division(unknown, rightExpr);
		Expression rightUnknown = new Division(leftExpr, unknown);
		Expression bothUnknown = new Division(unknown, unknown);
		assertEquals(Optional.empty(), leftUnknown.evaluate(s));
		assertEquals(Optional.empty(), rightUnknown.evaluate(s));
		assertEquals(Optional.empty(), bothUnknown.evaluate(s));
		
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new Division(leftExpr,null);});
		assertThrows(IllegalArgumentException.class, ()->{new Division(null,rightExpr);});
		assertThrows(IllegalArgumentException.class, ()->{new Division(null,null);});
		
		// Division by zero
		Expression zeroExpr = new NumberLiteral(0, Unit.ONE);
		Expression divideByZero = new Division(leftExpr, zeroExpr);
		assertEquals(Optional.empty(), divideByZero.evaluate(s));
	
	}
	
	@Test
	void NegationTest() {
		Expression expr = new NumberNegation(leftExpr);
		Amount expected = leftAmount.times(-1);
		assertEquals(Optional.of(expected), expr.evaluate(s));
		
		//Unknown values
		Expression valueUnknown = new NumberNegation(unknown);
		assertEquals(Optional.empty(), valueUnknown.evaluate(s));
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new NumberNegation(null);});
	}
	
	@Test
	void rootTest() {
		for (int n = 1; n <= 5; n++) {
			Expression<Amount> expr = new NumberRoot(leftExpr, n);
			Amount expected = leftAmount.root(n);
			assertEquals(expected, expr.evaluate(s).get());
			
			//Unknown
			Expression valueUnknown = new NumberRoot(unknown, n);
			assertEquals(Optional.empty(), valueUnknown.evaluate(s));
		}
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new NumberRoot(null, 2);});	
		//n = 0
		Expression<Amount> expr = new NumberRoot(leftExpr, 0);
		assertEquals(Optional.empty(), expr.evaluate(s));
	}
	
	@Test
	void absTest() {
		Expression<Amount> n = new NumberLiteral(5, Unit.ONE);
		Expression expr1 = new NumberAbsolute(n);
		Expression expr2 = new NumberAbsolute(new NumberNegation(n));
		assertEquals(expr1.evaluate(s), expr2.evaluate(s));
		
		//Unknown values
		Expression valueUnknown = new NumberAbsolute(unknown);
		assertEquals(Optional.empty(), valueUnknown.evaluate(s));
		//Nullcheck
		assertThrows(IllegalArgumentException.class, ()->{new NumberAbsolute(null);});
	}
}
