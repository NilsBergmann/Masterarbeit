package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

class IntervalTest {
	static DataController ctrl;
	static State s;
	
	@BeforeAll
	public static void init() {
		ctrl = new DataController(false);
		ctrl.connectToDatabase("Testcases.db");
		ctrl.selectTable("IntervalTest");
		// Table has timestamp 1000000000 to 1000000040 in 1ms steps
		ctrl.updateStates();
		// Pick state at 1000000020 as current state
		s = ctrl.getClosestState(Instant.ofEpochMilli(1000000020));
	}
	
	@Test
	void closedIntervalTest() {
		// Interval [0,10ms]
		Duration start = Duration.ofMillis(0);
		Duration end = Duration.ofMillis(10);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, true, true);
		
		// Add State to get absolute interval
		AbsoluteTimeInterval absInterval = interval.addInstant(s.timestamp);
		// Get matching states, should contain [...]20 to [...]30 for a total of 11
		List<State> states = ctrl.getStatesInInterval(absInterval);
		
		int count = states.size();
		assertEquals(11, count);
	}
	
	@Test
	void halfOpenIntervalTest() {
		// Interval (0,10ms]
		Duration start = Duration.ofMillis(0);
		Duration end = Duration.ofMillis(10);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, false, true);
		
		// Add State to get absolute interval
		AbsoluteTimeInterval absInterval = interval.addInstant(s.timestamp);
		// Get matching states,  should contain [...]21 to [...]30 for a total of 10
		List<State> states = ctrl.getStatesInInterval(absInterval);
		
		int count = states.size();
		assertEquals(10, count);
	}
	
	@Test
	void halfOpenIntervalTest2() {
		// Interval [0,10ms)
		Duration start = Duration.ofMillis(0);
		Duration end = Duration.ofMillis(10);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, true, false);
		
		// Add State to get absolute interval
		AbsoluteTimeInterval absInterval = interval.addInstant(s.timestamp);
		// Get matching states, should contain [...]20 to [...]29 for a total of 10
		List<State> states = ctrl.getStatesInInterval(absInterval);
		
		int count = states.size();
		assertEquals(10, count);
	}


	@Test
	void OpenIntervalTest() {
		// Interval (0,10ms)
		Duration start = Duration.ofMillis(0);
		Duration end = Duration.ofMillis(10);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, false, false);
		
		// Add State to get absolute interval
		AbsoluteTimeInterval absInterval = interval.addInstant(s.timestamp);
		// Get matching states,  should contain [...]21 to [...]29 for a total of 9
		List<State> states = ctrl.getStatesInInterval(absInterval);
		int count = states.size();
		assertEquals(9, count);
	}
	
	@Test
	void IntervalOutOfRangeTest() {
		Instant start = Instant.ofEpochMilli(1000000050);
		Instant end = Instant.ofEpochMilli(1000000090);
		AbsoluteTimeInterval absInterval = new AbsoluteTimeInterval(start, end, true, true);
		
		int count = ctrl.getStatesInInterval(absInterval).size();
		assertEquals(0, count);
	}
	
	@Test
	void PartialIntervalOutOfRangeTest() {
		Duration start = Duration.ofMillis(15);
		Duration end = Duration.ofMillis(10000000);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, true, true);
		
		// Add State to get absolute interval
		AbsoluteTimeInterval absInterval = interval.addInstant(s.timestamp);
		// Get matching states, should contain states from [...]35 to [...]40 for a total of 6: 35,36,37,38,39,40
		List<State> states = ctrl.getStatesInInterval(absInterval);
		
		int count = states.size();
		assertEquals(6, count);
	}
	
	@Test
	void negativeIntervalTest() {
		// Interval [-10ms,10ms]
		Duration start = Duration.ofMillis(-10);
		Duration end = Duration.ofMillis(10);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, true, true);
		
		// Add State to get absolute interval
		AbsoluteTimeInterval absInterval = interval.addInstant(s.timestamp);
		// Get matching states, should contain [...]10 to [...]30 for a total of 21
		List<State> states = ctrl.getStatesInInterval(absInterval);
		
		int count = states.size();
		assertEquals(21, count);
	}
		
	@Test
	void MaxMinIntervalTest() {
		// Create [-inf, +inf] interval
		Duration start = Duration.ofMillis(Long.MIN_VALUE);
		Duration end = Duration.ofMillis(Long.MAX_VALUE);
		RelativeTimeInterval interval = new RelativeTimeInterval(start, end, true, true);
		
		//Test various offset values to catch errors
		try {
		// t=0
		AbsoluteTimeInterval absInterval = interval.addInstant(Instant.ofEpochMilli(0));
		List<State> states = ctrl.getStatesInInterval(absInterval);
		
		// t=MIN_VALUE
		AbsoluteTimeInterval absIntervalMoveByMinimum = interval.addInstant(Instant.ofEpochMilli(Long.MIN_VALUE));
		List<State> statesMin = ctrl.getStatesInInterval(absIntervalMoveByMinimum);
		
		// t=MAX_VALUE
		AbsoluteTimeInterval absIntervalMoveByMaximum = interval.addInstant(Instant.ofEpochMilli(Long.MAX_VALUE));
		List<State> statesMax = ctrl.getStatesInInterval(absIntervalMoveByMaximum);
		
		// t=s.timestamp
		AbsoluteTimeInterval absIntervalState = interval.addInstant(s.timestamp);
		List<State> statesS = ctrl.getStatesInInterval(absIntervalState);
		} catch (Exception e) {
			fail("[-inf, +inf] interval failed");
		}
	}

}
