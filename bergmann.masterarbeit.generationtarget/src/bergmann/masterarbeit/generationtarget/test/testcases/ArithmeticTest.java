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
import bergmann.masterarbeit.generationtarget.expressions.Multiplication;
import bergmann.masterarbeit.generationtarget.expressions.NumberLiteral;
import bergmann.masterarbeit.generationtarget.expressions.NumberNegation;
import bergmann.masterarbeit.generationtarget.expressions.Subtraction;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class ArithmeticTest {
	static NumberLiteral leftExpr, rightExpr;
	static Amount leftAmount, rightAmount;
	static State s;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		double leftValue = 2.0;
		double rightValue = 5.0;
		
		Unit leftUnit = SI.KILOMETER;
		Unit rightUnit = SI.CENTIMETER;
		
		leftExpr = new NumberLiteral(leftValue, leftUnit);
		rightExpr = new NumberLiteral(rightValue, rightUnit);
		
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
	}
	
	@Test
	void SubtractionTest() {
		Expression expr = new Subtraction(leftExpr, rightExpr);
		Amount expected = leftAmount.minus(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
	}
	
	@Test
	void MultiplicationTest() {
		Expression expr = new Multiplication(leftExpr, rightExpr);
		Amount expected = leftAmount.times(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
	}
	
	@Test
	void DivisionTest() {
		Expression expr = new Division(leftExpr, rightExpr);
		Amount expected = leftAmount.divide(rightAmount);
		assertEquals(Optional.of(expected), expr.evaluate(s));
	}
	
	@Test
	void NegationTest() {
		Expression expr = new NumberNegation(leftExpr);
		Amount expected = leftAmount.times(-1);
		assertEquals(Optional.of(expected), expr.evaluate(s));
	}
	// TODO: Add tests for null cases and incompatible units

}
