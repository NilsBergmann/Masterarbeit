package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Instant;

public class State implements Comparable<State> {
    public Instant timestamp;

    public State(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int compareTo(State state) {
        return this.compareTo(this.timestamp);
    }

    public int compareTo(Instant timestamp) {
        if (this.timestamp.isBefore(timestamp))
            return -1;
        if (this.timestamp.equals(timestamp))
            return 0;
        else
            return 1;
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        } else {
            State otherS = (State) other;
            return this.timestamp.equals(otherS.timestamp);
        }
    }
}