package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.*;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.Unknown;

class BooleanOperators {
	static Expression t, f, unknown;
	static State s;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		t = new BoolLiteral(true);
		f = new BoolLiteral(false);
		unknown = new Unknown<Boolean>();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void AndTest() {
		Expression t_t = new And(t, t);
		Expression t_f = new And(t, f);
		Expression t_u = new And(t, unknown);
		
		Expression f_t = new And(f, t);
		Expression f_f = new And(f, f);
		Expression f_u = new And(f, unknown);
		
		Expression u_t = new And(unknown, t);
		Expression u_f = new And(unknown, f);
		Expression u_u = new And(unknown, unknown);
		
		assertEquals(false, f_f.evaluate(s).get());
		assertEquals(false, f_t.evaluate(s).get());
		assertEquals(false, t_f.evaluate(s).get());
		assertEquals(true,  t_t.evaluate(s).get());

		//Unknown && false -> false
		assertEquals(false, u_f.evaluate(s).get());
		assertEquals(false, f_u.evaluate(s).get());
		
		//Unknown && true -> Unknown
		assertEquals(Optional.empty(), u_t.evaluate(s));
		assertEquals(Optional.empty(), t_u.evaluate(s));
		
		// Unknown && Unknown -> Unknown
		assertEquals(Optional.empty(), u_u.evaluate(s));
	}
	
	@Test
	void OrTest() {
		Expression t_t = new Or(t, t);
		Expression t_f = new Or(t, f);
		Expression t_u = new Or(t, unknown);
		
		Expression f_t = new Or(f, t);
		Expression f_f = new Or(f, f);
		Expression f_u = new Or(f, unknown);
		
		Expression u_t = new Or(unknown, t);
		Expression u_f = new Or(unknown, f);
		Expression u_u = new Or(unknown, unknown);
		
		assertEquals(false, f_f.evaluate(s).get());
		assertEquals(true,  f_t.evaluate(s).get());
		assertEquals(true,  t_f.evaluate(s).get());
		assertEquals(true,  t_t.evaluate(s).get());

		//Unknown || false -> Unknown
		assertEquals(Optional.empty(), u_f.evaluate(s));
		assertEquals(Optional.empty(), f_u.evaluate(s));
		
		//Unknown || true -> true
		assertEquals(true, u_t.evaluate(s).get());
		assertEquals(true, t_u.evaluate(s).get());
		
		// Unknown || Unknown -> Unknown
		assertEquals(Optional.empty(), u_u.evaluate(s));
	}
	
	@Test
	void ImplicationTest() {
		Expression t_t = new Implication(t, t);
		Expression t_f = new Implication(t, f);
		Expression t_u = new Implication(t, unknown);
		
		Expression f_t = new Implication(f, t);
		Expression f_f = new Implication(f, f);
		Expression f_u = new Implication(f, unknown);
		
		Expression u_t = new Implication(unknown, t);
		Expression u_f = new Implication(unknown, f);
		Expression u_u = new Implication(unknown, unknown);
		
		// https://en.wikipedia.org/wiki/Three-valued_logic#Kleene_and_Priest_logics
		
		assertEquals(true,  f_f.evaluate(s).get());
		assertEquals(true,  f_t.evaluate(s).get());
		assertEquals(false,  t_f.evaluate(s).get());
		assertEquals(true,  t_t.evaluate(s).get());
		
		// left side unknown
		assertEquals(Optional.of(true), u_t.evaluate(s));
		assertEquals(Optional.empty(), u_f.evaluate(s));
		// right side unknown
		assertEquals(Optional.empty(), t_u.evaluate(s));
		assertEquals(Optional.of(true), f_u.evaluate(s));
		// both unknown
		assertEquals(Optional.empty(), u_u.evaluate(s));
	}
	
	@Test
	void NegationTest() {
		Expression neg_t = new BoolNegation(t);
		Expression neg_f = new BoolNegation(f);
		Expression neg_unknown = new BoolNegation(unknown);
		
		assertEquals(false, neg_t.evaluate(s).get());
		assertEquals(true,  neg_f.evaluate(s).get());
		assertEquals(Optional.empty(), neg_unknown.evaluate(s));
	}	
}
