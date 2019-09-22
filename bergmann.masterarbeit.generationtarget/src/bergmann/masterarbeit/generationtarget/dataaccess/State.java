package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Instant;

public class State implements Comparable<State> {
    public Instant timestamp;

    public State(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int compareTo(State state) {
        if (this.timestamp.isBefore(state.timestamp))
            return -1;
        if (this.timestamp.equals(state.timestamp))
            return 0;
        else
            return 1;
    }
}