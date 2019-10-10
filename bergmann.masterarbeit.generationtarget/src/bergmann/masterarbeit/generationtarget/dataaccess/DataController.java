package bergmann.masterarbeit.generationtarget.dataaccess;

import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.Assertion;
import bergmann.masterarbeit.generationtarget.utils.UserVariable;

public class DataController {
    DatabaseWrapper dbWrapper;
    List<State> states;
    boolean isRealTime = false;

    public DataController(boolean isRealTime) {
        states = new ArrayList<State>();
        this.isRealTime = isRealTime;
    }

    public void connectToDatabase(String path) {
        dbWrapper = new DatabaseWrapper(path);
    }

    public DatabaseWrapper getDatabaseWrapper() {
        return this.dbWrapper;
    }

    public void updateStates() {
        this.states = this.dbWrapper.getStates();
    }

    public State getCurrentState() {
        return states.get(states.size() - 1);
    }

    public State getClosestState(Instant timestamp) {
        Duration minimumDistance = Duration.ofMillis(Long.MAX_VALUE);
        State minimumState = null;
        for (State state : this.states) {
            Duration distance = Duration.between(state.timestamp, timestamp);
            if (distance.abs().compareTo(minimumDistance) > 0) {
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
            return states.subList(0, index);
        } catch (Exception e) {
            return new ArrayList<State>();
        }
    }

    public List<State> getAllStatesAfter(State state) {
        int index = states.indexOf(state);
        try {
            return states.subList(index + 1, states.size());
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

    public Optional<Object> getValue(State state, String ID) {
        // Todo: Implement this
        dbWrapper.g
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
        System.out.println("Running evaluations...");
        // Assertions
        Map<String, Map<State, Optional>> resultsAssertions = new HashMap<String, Map<State, Optional>>();
        for (Assertion assertion : assertions) {
            System.out.println(assertion.name);
            Map<State, Optional> res = new HashMap<State, Optional>();
            for (State s : this.states) {
                res.put(s, assertion.evaluateAt(s, this));
            }
            resultsAssertions.put(assertion.name, res);
        }
        // User Variables
        Map<String, Map<State, Optional>> resultsUserVars = new HashMap<String, Map<State, Optional>>();
        for (UserVariable uv : userVars) {
            System.out.println(uv.name);
            Map<State, Optional> res = new HashMap<State, Optional>();
            for (State s : this.states) {
                res.put(s, uv.evaluate(s, this));
            }
            resultsUserVars.put(uv.name, res);
        }
        System.out.println("Done, saving results");
        writeToCSV(resultsAssertions, "Test_Assertions.txt");
        writeToCSV(resultsUserVars, "Test_Uservars.txt");
    }

    public void writeToCSV(Map<String, Map<State, Optional>> result, String path) {
        if (result.size() == 0)
            return;
        // TODO: Actually print csv
        for (Map.Entry<String, Map<State, Optional>> entry : result.entrySet()) {
            String expr = entry.getKey();
            try {
                String CSVpath = expr + ".txt";
                System.out.println("Writing results to " + CSVpath);
                FileWriter fw = new FileWriter(path);
                for (Map.Entry<State, Optional> r : entry.getValue().entrySet()) {
                    State state = r.getKey();
                    Optional x = r.getValue();
                    String value = x.isPresent() ? x.get().toString() : "UNKNOWN";
                    fw.write(state.timestamp + " -> " + value.toString() + "\n");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}