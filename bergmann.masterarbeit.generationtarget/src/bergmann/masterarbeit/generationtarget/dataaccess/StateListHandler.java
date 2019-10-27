/**
 * 
 */
package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;

/**
 * 
 *
 */
public class StateListHandler {
    List<State> states;
    boolean realTimeEvaluationMode;
    
    public StateListHandler() {
    	this(false);
    }
   
    
    public StateListHandler(boolean realTimeEvaluationMode) {
    	states = new ArrayList<State>();
    	this.realTimeEvaluationMode = realTimeEvaluationMode;
	}

	public State createStateFromTimestamp(Instant timestamp) {
		State s = new State(timestamp);
		this.add(s);
    	return s;
    }
    
    public State createStateFromEpochMilli(long epochMillis) {
    	return this.createStateFromTimestamp(Instant.ofEpochMilli(epochMillis));
    }
    
    public void add(State s) {
    	this.states.add(s);
    	s.setStateListHandler(this);
    	Collections.sort(this.states);
    }
    
    public boolean contains(State s) {
    	return this.states.contains(s);
    }
    
    public void addAll(Collection<? extends State> states) {
    	this.states.addAll(states);
    	for (State state : states) {
    		state.setStateListHandler(this);
		}
    	Collections.sort(this.states);
    }
    
    public State getLatestState() {
        return states.get(states.size() - 1);
    }
    
    public State getFirstState() {
        return states.get(0);
    }
    
    public List<State> getAllStates() {
        return new ArrayList<State>(this.states);
    }
    
    public State getStateOffsetBy(State start, int amount) {
    	int startPos = this.states.indexOf(start);
    	if (startPos < 0)
    		return null;
    	int targetPos = startPos + amount;
    	if (targetPos < 0 || targetPos >= states.size())
    		return null;
    	return this.states.get(targetPos);
    }
    
    public State getClosestState(Instant timestamp) {
        Duration minimumDistance = Duration.ofMillis(Long.MAX_VALUE);
        State minimumState = null;
        for (State state : this.states) {
            Duration distance = Duration.between(state.timestamp, timestamp);
            if (distance.abs().compareTo(minimumDistance) < 0) {
                minimumDistance = distance;
                minimumState = state;
            }
        }
        return minimumState;
    }
    
    public List<State> getStatesInInterval(AbsoluteTimeInterval interval) {
        List<State> retVal = new ArrayList<State>();
        for (State state : this.states) {
            if (interval.contains(state.timestamp)) {
                retVal.add(state);
            }
        }
        return retVal;
    }
    
    public State getPreviousState(State state) {
        int index = states.indexOf(state);
        if (index > 0 && index <= states.size() - 1) {
            return states.get(index - 1);
        }
        return null;
    }

    public State getFollowingState(State state) {
        int index = states.indexOf(state);
        if (index >= 0 && index < states.size() - 1) {
            return states.get(index + 1);
        }
        return null;
    }
    
    public List<State> getAllStatesBefore(State state) {
        int index = states.indexOf(state);
        try {
            return new ArrayList(states.subList(0, index));
        } catch (Exception e) {
            return new ArrayList<State>();
        }
    }
    
    public List<State> getAllStatesAfter(State state) {
        int index = states.indexOf(state);
        try {
            return new ArrayList(states.subList(index + 1, states.size()));
        } catch (Exception e) {
            return new ArrayList<State>();
        }
    }
    
    public boolean intervalIsInRange(AbsoluteTimeInterval interval) {
        if (states.isEmpty())
            return false;
        Instant lowestAllowedTimestamp = this.getFirstState().timestamp;
        Instant highestAllowedTimestamp = this.getLatestState().timestamp;
        // Check start 
        boolean startOK = false;
        if(interval.includeLeft)
        	startOK = lowestAllowedTimestamp.isBefore(interval.start) || lowestAllowedTimestamp.equals(interval.start);
        else
        	startOK = lowestAllowedTimestamp.isBefore(interval.start);
        // Check end
        boolean endOK = false;
        if(interval.includeRight)
        	endOK = highestAllowedTimestamp.isAfter(interval.end) || highestAllowedTimestamp.equals(interval.end);
        else
        	endOK = highestAllowedTimestamp.isAfter(interval.end);
        return startOK && endOK;
    }
    
    public boolean isRealTimeEvaluationMode() {
		return realTimeEvaluationMode;
	}

	public void setRealTimeEvaluationMode(boolean realTimeEvaluationMode) {
		this.realTimeEvaluationMode = realTimeEvaluationMode;
	}
    public boolean isEmpty() {
    	return states.isEmpty();
    }
}
