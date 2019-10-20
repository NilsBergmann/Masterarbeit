package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
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
