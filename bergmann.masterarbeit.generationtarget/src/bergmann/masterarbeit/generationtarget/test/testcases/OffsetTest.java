package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.StandaloneDataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.NumberDomainValue;
import bergmann.masterarbeit.generationtarget.expressions.OffsetByStates;
import bergmann.masterarbeit.generationtarget.expressions.OffsetByTime;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.test.utils.TestMonitorDeclaration;

class OffsetTest {
	static StandaloneDataController ctrl;
	static Expression<Amount> subExpr;
	static Unit unit;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		unit = NonSI.KILOMETERS_PER_HOUR;

		ctrl = new StandaloneDataController(false);
		ctrl.connectToDatabase("Testcases.db");

		TestMonitorDeclaration decl = new TestMonitorDeclaration();
		decl.addDomainAmount("Vehicle1ForwardSpeed", unit);
		ctrl.registerRequiredData(decl);

		ctrl.selectTable("Brian_Scenario_Example");

		subExpr = new NumberDomainValue("Vehicle1ForwardSpeed");
	}

	@Test
	void OffsetByStatesTest() {
		int offset = -4;
		Expression e = new OffsetByStates(subExpr, offset);
		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional result = e.evaluate(state);
			State target = ctrl.stateHandler.getStateOffsetBy(state, offset);
			if (target == null)
				assertEquals(Optional.empty(), result);
			else {
				Optional expected = subExpr.evaluate(target);
				assertEquals(expected, result, "Timestamp:" + state.timestamp.toEpochMilli());
			}
		}
	}

	@Test
	void OffsetByTimeTest() {
		Duration offset = Duration.ofMillis(1); // Roughly 4 states
		Expression e = new OffsetByTime(subExpr, offset);
		for (State state : ctrl.stateHandler.getAllStates()) {
			Optional result = e.evaluate(state);

			Instant expectedTimestamp = state.timestamp.plus(offset);
			State target = ctrl.stateHandler.getClosestState(expectedTimestamp);

			if (target == null)
				assertEquals(Optional.empty(), result);
			else {
				Optional expected = subExpr.evaluate(target);
				assertEquals(expected, result, "Timestamp:" + state.timestamp.toEpochMilli());
			}
		}
	}

}
