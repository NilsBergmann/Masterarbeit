package bergmann.masterarbeit.generationtarget.dataaccess;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import javax.measure.unit.Unit;

import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.Assertion;
import bergmann.masterarbeit.generationtarget.utils.UserVariable;

public class DataController {
    private DatabaseWrapper dbWrapper;
    List<State> states;
    public boolean isRealTime = false;

    public DataController(boolean isRealTime) {
        states = new ArrayList<State>();
        this.isRealTime = isRealTime;
        this.dbWrapper = new DatabaseWrapper();
    }

    public void connectToDatabase(String path) {
        if (dbWrapper.isConnected()) {
            dbWrapper.disconnect();
        }
        dbWrapper.connect(path);
    }

    public void updateStates() {
        this.states = this.dbWrapper.getStates();
        for (State state : states) {
            state.dataController = this;
        }
    }

    public void selectTable(String tablename) {
        if (this.dbWrapper != null && this.dbWrapper.isConnected()) {
            try {
                this.dbWrapper.setTable(tablename);
                this.updateStates();
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }
    }

    public List<String> getTables() {
        if (this.dbWrapper != null && this.dbWrapper.isConnected()) {
            return this.dbWrapper.getTables();
        }
        return new ArrayList<String>();
    }

    public boolean isConnectedToDB() {
        if (this.dbWrapper == null)
            return false;
        else
            return this.dbWrapper.isConnected();
    }

    public State getLatestState() {
        return states.get(states.size() - 1);
    }

    public State getFirstState() {
        return states.get(0);
    }

    public State getStateOffsetBy(State start, int amount) {
        int startPos = this.states.indexOf(start);
        if (startPos == -1)
            return null;
        int targetPos = startPos + amount;
        if (targetPos < 0 || targetPos >= states.size())
            return null;
        return this.states.get(targetPos);
    }

    public List<State> getAllStates() {
        return this.states;
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

    public boolean timestampIsInRange(Instant timestamp, boolean included) {
        if (states.isEmpty() || timestamp == null)
            return false;
        int diff = states.get(states.size() - 1).timestamp.compareTo(timestamp);
        if (included)
            return diff >= 0;
        else
            return diff > 0;
    }

    public boolean intervalIsInRange(AbsoluteTimeInterval interval) {
        boolean start = timestampIsInRange(interval.start, interval.includeLeft);
        boolean end = timestampIsInRange(interval.end, interval.includeRight);
        return start && end;
    }

    public boolean isRealTime() {
        return this.isRealTime;
    }

    public void registerNumberDBColumn(String name, Unit unit) {
        this.dbWrapper.registerNumberColumn(name, unit);
    }

    public void registerStringDBColumn(String name) {
        this.dbWrapper.registerStringColumn(name);
    }

    public void registerBooleanDBColumn(String name) {
        this.dbWrapper.registerBooleanColumn(name);
    }

    public void runEvaluation(List<Assertion> assertions, List<UserVariable> userVars, String tableName) {
        if (this.isRealTime) {
            System.err.println("real time not implemented yet");
            // TODO: Implement real time mode
        } else {
            this.runNonRealtimeEvaluation(assertions, userVars, tableName);
        }
    }

    private void runNonRealtimeEvaluation(List<Assertion> assertions, List<UserVariable> userVars, String tableName) {
        this.dbWrapper.setTable(tableName);
        // Get states
        this.updateStates();
        // Evaluate States
        System.out.println("Running evaluations...\n");
        
        for (State state : this.getAllStates()) {
        	System.out.println("------------------");
            System.out.println("State " + state.toString()+"\n");
            for (UserVariable userVariable : userVars) {
                Optional result = userVariable.evaluate(state);
                System.out.println("UserVar: " + userVariable.name + " -> " + result);
            }
            System.out.println("");
            for (Assertion assertion : assertions) {
                Optional result = assertion.evaluateAt(state);
                System.out.println("Assertion: " + assertion.name + " -> " + result);
            }
            System.out.println("");
        }
        System.out.println("Evaluation complete!");
        writeToCSV("Test.csv", states);
    }
    
    private void writeToCSV(String path, List<State> states) {
    	System.out.println("Writing data to " + path);
    	Set<String> storedValues = new LinkedHashSet<String>();
    	for (State state : states) {
			storedValues.addAll(state.getStoredKeys());
		}
    	try {
    	//Open file
   		FileWriter fw = new FileWriter(path);
   		
    	// Create header
    	String header = "Timestamp; ";
    	for (String string : storedValues) {
    		header += string +";";
    	}
    	header+="\n";
    	
    	// Write state data
    	fw.write(header);
    	for (State state : states) {
			String current = state.timestamp.toString()+ ";";
			for (String key : storedValues) {
				Optional data = state.getStored(key);
				String dataString = data.isPresent() ? data.get().toString() : "UNKNOWN";
				current += dataString + ";";
			}
			current+="\n";
			fw.write(current);
		}
    	fw.close();
    	System.out.println("Completed!");
    	} catch (IOException e) {
    		System.out.println("Writing results to csv failed. " + e);
    	}
    }
    
}