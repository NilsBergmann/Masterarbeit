package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BoolDatabaseAccess;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class StateInformationTest {
	static DataController ctrl;
	static Expression a, b, expected;
	
	@BeforeAll
	public static void init() {
		ctrl = new DataController(false);
		ctrl.connectToDatabase("Testcases.db");
		ctrl.registerStringDBColumn("Vehicle0ACCState");
		ctrl.selectTable("Brian_Scenario_Example");
	}
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void test() {
		List<State> states = ctrl.getAllStates();
		System.out.println("start1");
		for (State state : states) {
			System.out.println(state.toLongString());
			state.getDBString("Vehicle0ACCState");
		}
		System.out.println("done2");
		fail("Not yet implemented");
	}

}