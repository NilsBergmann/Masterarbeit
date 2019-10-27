package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.StandaloneDataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class StateInformationTest {
	static StandaloneDataController ctrl;
	static Expression a, b, expected;
	
	@BeforeAll
	public static void init() {
		ctrl = new StandaloneDataController(false);
		ctrl.connectToDatabase("Testcases.db");
		ctrl.registerStringDBColumn("Vehicle0ACCState");
		ctrl.selectTable("Brian_Scenario_Example");
	}
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void test() {
		List<State> states = ctrl.stateHandler.getAllStates();
		for (State state : states) {
			state.getDBString("Vehicle0ACCState");
		}
		fail("Not yet implemented");
	}

}
