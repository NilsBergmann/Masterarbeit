package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class State implements Comparable<State> {
    public Instant timestamp;
    public DataController ctrl;
    Map<String, Object> stateData;

    public State(Instant timestamp, DataController ctrl) {
        this.timestamp = timestamp;
        this.ctrl = ctrl;
        stateData = new HashMap<String, Object>();
    }

    public State(Instant timestamp) {
        this(timestamp, null);
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object obj = null;
        if (stateData.containsKey(key)) {
            // Return cached if existing
            obj = stateData.get(key);
        } else if (this.ctrl != null) {
            // try getting data matching key from dataControl
            Optional tmp = ctrl.getValue(this, key);
            if (tmp.isPresent())
                obj = tmp.get();
        }
        // Check if obj is right class
        if (obj != null && obj.getClass().equals(clazz)) {
            T retVal = clazz.cast(obj);
            return Optional.of(retVal);
        }
        return Optional.empty();
    }

    private void store(Object o, String key) {
        if (o == null || key == null)
            return;
        if (o instanceof Optional) {
            Optional oOptional = (Optional) o;
            if (oOptional.isPresent()) {
                store(oOptional.get(), key);
            } else {
                return;
            }
        } else {
            stateData.put(key, o);
        }

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