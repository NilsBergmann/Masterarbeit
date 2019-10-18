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
import bergmann.masterarbeit.generationtarget.expressions.BoolNegation;
import bergmann.masterarbeit.generationtarget.expressions.Implication;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Finally;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Global;
import bergmann.masterarbeit.generationtarget.expressions.LTL_Next;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class PLTLOperatorsTest {
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
		fail("Not implemented yet");
	}
	
	@Test
	void sinceTest() {
		fail("Not implemented yet");
	}
	
	@Test
	void YesterdayTest() {
		fail("Not implemented yet");
	}
	
	@Test
	void ZTest() {
		fail("Not implemented yet");
	}
	
	@Test
	void TriggerTest() {
		fail("Not implemented yet");
	}
}
