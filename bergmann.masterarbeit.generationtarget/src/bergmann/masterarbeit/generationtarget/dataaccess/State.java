package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.utils.MonitorDeclaration;
import bergmann.masterarbeit.generationtarget.utils.UserVariable;

public class State implements Comparable<State> {
    public Instant timestamp;
    public StateListHandler stateListHandler;
    public Map<String, Optional> storedDomainValues;
    public Map<String, Optional> storedUserVariables;
    public Map<String, Optional> storedAssertions;

    public State(Instant timestamp) {
        this.timestamp = timestamp;
        storedDomainValues = new HashMap<String, Optional>();
        storedUserVariables = new HashMap<String, Optional>();
        storedAssertions = new HashMap<String, Optional>();
    }

    public State(Instant timestamp, MonitorDeclaration declr) {
        this(timestamp);
        if (declr != null)
            this.initUnknowns(declr);
    }

    public State(long epochMillis) {
        this(Instant.ofEpochMilli(epochMillis));
    }

    public State(long epochMillis, MonitorDeclaration declr) {
        this(Instant.ofEpochMilli(epochMillis), declr);
    }

    /**
     * DomainValue stuff
     */

    public void initUnknowns(MonitorDeclaration decl) {
        for (String identifier : decl.getRequiredDataBooleans()) {
            if (!this.storedDomainValues.containsKey(identifier))
                this.storedDomainValues.put(identifier, Optional.empty());
        }
        for (String identifier : decl.getRequiredDataNumbers().keySet()) {
            if (!this.storedDomainValues.containsKey(identifier))
                this.storedDomainValues.put(identifier, Optional.empty());
        }
        for (String identifier : decl.getRequiredDataStrings()) {
            if (!this.storedDomainValues.containsKey(identifier))
                this.storedDomainValues.put(identifier, Optional.empty());
        }
        for (String identifier : decl.getDeclaredAssertionNames()) {
            if (!this.storedAssertions.containsKey(identifier))
                this.storedAssertions.put(identifier, Optional.empty());
        }
        for (String identifier : decl.getDeclaredUserVariableNames()) {
            if (!this.storedUserVariables.containsKey(identifier))
                this.storedUserVariables.put(identifier, Optional.empty());
        }
    }

    public Optional<Boolean> getDomainBoolean(String key) {
        if (storedDomainValues.containsKey(key)) {
            Optional value = storedDomainValues.get(key);
            if (value.isPresent() && !(value.get() instanceof Boolean)) {
                System.err.println(this.toString() + " has no boolean " + key + ", instead received a "
                        + value.get().getClass() + ". Returning Optional.empty");
                return Optional.empty();
            }
            return value;
        } else
            System.err.println(this.toString() + " has no boolean " + key + ". Returning Optional.empty");
        return Optional.empty();
    }

    public Optional<Amount> getDomainAmount(String key) {
        if (storedDomainValues.containsKey(key)) {
            Optional value = storedDomainValues.get(key);
            if (value.isPresent() && !(value.get() instanceof Amount)) {
                System.err.println(this.toString() + " has no Amount " + key + ", instead received a "
                        + value.get().getClass() + ". Returning Optional.empty");
                return Optional.empty();
            }
            return value;
        } else
            System.err.println(this.toString() + " has no Amount " + key + ". Returning Optional.empty");
        return Optional.empty();
    }

    public Optional<String> getDomainString(String key) {
        if (storedDomainValues.containsKey(key)) {
            Optional value = storedDomainValues.get(key);
            if (value.isPresent() && !(value.get() instanceof String)) {
                return Optional.empty();
            }
            return value;
        } else
            System.err.println(this.toString() + " has no String " + key + ". Returning Optional.empty");
        return Optional.empty();
    }

    public Set<String> getStoredDomainIDs() {
        return this.storedDomainValues.keySet();
    }

    public void storeDomainValue(String key, Optional value) {
        if (value == null || key == null)
            throw new IllegalArgumentException(
                    "Can't store domain value because at least one parameter is null: (" + key + ", " + value + ")");
        storedDomainValues.put(key, value);
    }

    public <T extends Object> Optional<T> getDomainValue(String key) {
        if (storedDomainValues.containsKey(key)) {
            Optional obj = storedDomainValues.get(key);
            if (obj != null)
                return obj;
        } else {
            System.err.println("No domain value '" + key + "' is stored in this state. Returning Optional.empty.");
        }
        return Optional.empty();
    }

    /**
     * UserVariable stuff
     */

    public Set<String> getStoredUserVariableIDs() {
        return this.storedUserVariables.keySet();
    }

    public void storeUserVariableResult(String key, Optional value) {
        if (value == null || key == null)
            throw new IllegalArgumentException(
                    "Can't store domain value because at least one parameter is null: (" + key + ", " + value + ")");
        this.storedUserVariables.put(key, value);
    }

    public Optional getStoredUserVariableResult(String key) {
        Optional obj = Optional.empty();
        if (storedUserVariables.containsKey(key)) {
            // Return cached if it exists
            obj = storedUserVariables.get(key);
            if (obj != null)
                return obj;
        } else {
            System.err
                    .println("No UserVariable result '" + key + "' is stored in this state. Returning Optional.empty.");
        }
        return Optional.empty();
    }

    /**
     * Assertion stuff
     */

    public Set<String> getStoredAssertionIDs() {
        return this.storedAssertions.keySet();
    }

    public void storeAssertionResult(String key, Optional<Boolean> value) {
        if (value == null || key == null)
            throw new IllegalArgumentException(
                    "Can't store domain value because at least one parameter is null: (" + key + ", " + value + ")");
        this.storedAssertions.put(key, value);
    }

    public Optional<Boolean> getAssertionResult(String key) {
        Optional obj = Optional.empty();
        if (storedAssertions.containsKey(key)) {
            // Return cached if it exists
            obj = storedAssertions.get(key);
            if (obj != null)
                return obj;
        } else {
            System.err.println("No AssertionResult '" + key + "' is stored in this state. Returning Optional.empty.");
        }
        return Optional.empty();
    }

    /**
     * General stuff
     */

    public int compareTo(State state) {
        return this.compareTo(state.timestamp);
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
            return this.timestamp.equals(otherS.timestamp) && this.storedDomainValues.equals(otherS.storedDomainValues)
                    && this.storedAssertions.equals(otherS.storedAssertions)
                    && this.storedUserVariables.equals(otherS.storedUserVariables);
        }
    }

    public String toString() {
        return "State[" + timestamp.toEpochMilli() + "]";
    }

    public String toLongString() {
        return this.toString() + " -> " + this.storedDataToString();
    }

    private String storedDataToString() {
        return "DomainValues=" + this.storedDomainValues.toString() + " UserVariables="
                + this.storedUserVariables.toString() + " Assertions=" + this.storedAssertions.toString();
    }

    public StateListHandler getStateListHandler() {
        return stateListHandler;
    }

    public void setStateListHandler(StateListHandler stateListHandler) {
        this.stateListHandler = stateListHandler;
    }
}