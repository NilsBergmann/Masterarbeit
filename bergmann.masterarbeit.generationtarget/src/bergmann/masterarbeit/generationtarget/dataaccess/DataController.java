package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;

public class DataController {
    DatabaseWrapper dbWrapper;
    ArrayList<State> states;

    public DataController() {
        states = new ArrayList<State>();
    }

    public void connectToDatabase(String path) {
        dbWrapper = new DatabaseWrapper(path);
        // TODO: Get timestamps from database, create states
    }

    public DatabaseWrapper getDatabaseWrapper() {
        return this.dbWrapper;
    }

    public void addState(Instant timestamp) {
        State newState = new State(timestamp);
        states.add(newState);
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
                // Improvement: Optimize taking into account the list should be ordered
            }
        }
        return minimumState;
    }

    public ArrayList<State> getStatesInInterval(AbsoluteTimeInterval interval) {
        System.out.println("getStatesInRange(): Not implemented yet");
        return null;
        // TODO Implement this. Consider open and closed time intervals
    }

    public boolean simulationComplete() {
        // Todo: add Flag to check if more states will be added later
        return true;
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

    public boolean timestampIsInRange(Instant timestamp) {
        if (states.isEmpty() || timestamp == null)
            return false;
        return states.get(states.size() - 1).timestamp.compareTo(timestamp) >= 0;
        // TODO Consider simulationCompleted()
    }

    public boolean intervalIsInRange(AbsoluteTimeInterval interval) {
        // TODO: Implement
        System.out.println("intervalIsInRange(). Not implemented");
        return true;
    }
}