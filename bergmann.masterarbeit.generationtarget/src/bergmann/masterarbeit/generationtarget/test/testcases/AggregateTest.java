package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.StandaloneDataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.AggregateAverage;
import bergmann.masterarbeit.generationtarget.expressions.AggregateMaximum;
import bergmann.masterarbeit.generationtarget.expressions.AggregateMinimum;
import bergmann.masterarbeit.generationtarget.expressions.NumberDatabaseAccess;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

class AggregateTest {
	static StandaloneDataController ctrl;
	static Expression<Amount> subExpr;
	static RelativeTimeInterval interval;
	static Unit unit;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		interval = new RelativeTimeInterval(Duration.ofMillis(-2), Duration.ofMillis(1), true, true);
		unit = SI.CENTIMETER.times(SI.SECOND);
		
		ctrl = new StandaloneDataController(false);
		ctrl.connectToDatabase("Testcases.db");
		ctrl.registerNumberDBColumn("value", unit);
		
		ctrl.selectTable("AggregateTest");

		subExpr = new NumberDatabaseAccess("value");
	}

	@Test
	void avgTest() {
		Expression e = new AggregateAverage(subExpr,interval);
		
		loop: for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Amount> realResult = e.evaluate(state);
			
			// Manually calculate avg to compare
			AbsoluteTimeInterval abs = interval.addInstant(state.timestamp);
			// If Interval is not in data: expect Optional.empty
			if(!ctrl.stateHandler.intervalIsInRange(abs)) {
				assertEquals(Optional.empty(), realResult);
				continue loop;
			}
			
			List<State> relevantStates = ctrl.stateHandler.getStatesInInterval(abs);
			Amount avg = Amount.valueOf(0,unit);
			for (State i : relevantStates) {
				Optional<Amount> current = subExpr.evaluate(i);
				if(current.isPresent())
					avg = avg.plus(current.get());
				else{
					assertEquals(Optional.empty(), realResult);
					continue loop;
				}				
			}
			avg = avg.divide(relevantStates.size());
			
			// Check approximation in case of rounding errors
			if(!realResult.isPresent() || !avg.approximates(realResult.get())) 
				fail(state.timestamp.toEpochMilli() + ": Expected" + Optional.of(avg) + " but was " + realResult);
			
			
		}
	}
	

	@Test
	void minTest() {
		Expression e = new AggregateMinimum(subExpr,interval);
		
		loop: for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Amount> realResult = e.evaluate(state);
			
			// Manually calculate min to compare
			AbsoluteTimeInterval abs = interval.addInstant(state.timestamp);
			// If Interval is not in data: expect Optional.empty
			if(!ctrl.stateHandler.intervalIsInRange(abs)) {
				assertEquals(Optional.empty(), realResult);
				continue loop;
			}
	
			List<State> relevantStates = ctrl.stateHandler.getStatesInInterval(abs);
			Amount min = Amount.valueOf(Long.MAX_VALUE,unit);
			for (State i : relevantStates) {
				Optional<Amount> current = subExpr.evaluate(i);
				if(!current.isPresent()) {
					assertEquals(Optional.empty(), realResult);
					continue loop;
				}
				if(min.isGreaterThan(current.get())) 
					min = current.get();
			}
			if(!realResult.isPresent() || !min.approximates(realResult.get())) {
				fail(state.timestamp.toEpochMilli() + ": Expected" + Optional.of(min) + " but was " + realResult);
			}
		}
	}
	

	@Test
	void maxTest() {
		Expression e = new AggregateMaximum(subExpr,interval);
		
		loop: for (State state : ctrl.stateHandler.getAllStates()) {
			Optional<Amount> realResult = e.evaluate(state);
			
			// Manually calculate max to compare
			AbsoluteTimeInterval abs = interval.addInstant(state.timestamp);
			// If Interval is not in data: expect Optional.empty
			if(!ctrl.stateHandler.intervalIsInRange(abs)) {
				assertEquals(Optional.empty(), realResult);
				continue loop;
			}
	
			List<State> relevantStates = ctrl.stateHandler.getStatesInInterval(abs);
			Amount max = Amount.valueOf(Long.MIN_VALUE,unit);
			for (State i : relevantStates) {
				Optional<Amount> current = subExpr.evaluate(i);
				if(!current.isPresent()) {
					assertEquals(Optional.empty(), realResult);
					continue loop;
				}
				if(max.isLessThan(current.get())) 
					max = current.get();
			}
			if(!realResult.isPresent() || !max.approximates(realResult.get())) {
				fail(state.timestamp.toEpochMilli() + ": Expected" + Optional.of(max) + " but was " + realResult);
			}
		}
	}

}
