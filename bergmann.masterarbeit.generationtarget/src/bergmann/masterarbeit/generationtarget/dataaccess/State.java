package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.utils.UserVariable;

public class State implements Comparable<State> {
    public Instant timestamp;
    public StateListHandler stateListHandler;
	public Map<String, Optional> storedDBValues;
    public Map<String, Optional> storedUserVariables;
    public Map<String, Optional> storedAssertions;

    public State(Instant timestamp) {
        this.timestamp = timestamp;
        storedDBValues = new HashMap<String, Optional>();
        storedUserVariables = new HashMap<String, Optional>();
        storedAssertions = new HashMap<String, Optional>();
    }

    
    /** 
     * Database value stuff 
     */
   
    public Optional<Boolean> getDBBoolean(String key) {
    	if(storedDBValues.containsKey(key)) {
    		Optional value = storedDBValues.get(key);
    		if(value.isPresent() && !(value.get() instanceof Boolean) ) {
    			System.err.println(this.toString() + " has no boolean " + key + ", instead received a " + value.get().getClass() + ". Returning Optional.empty");
    			return Optional.empty();
    		}
			return value;
		}
    	else	
    		System.err.println(this.toString() + " has no boolean " + key + ". Returning Optional.empty");
    	return Optional.empty();
    }
    
    public Optional<Amount> getDBAmount(String key) {
    	if(storedDBValues.containsKey(key)) {
    		Optional value = storedDBValues.get(key);
    		if(value.isPresent() && !(value.get() instanceof Amount) ) {
    			System.err.println(this.toString() + " has no Amount " + key + ", instead received a " + value.get().getClass() + ". Returning Optional.empty");
    			return Optional.empty();
    		}
			return value;
    	}
    	else	
    		System.err.println(this.toString() + " has no Amount " + key + ". Returning Optional.empty");
    	return Optional.empty();
    }
    
    public Optional<String> getDBString(String key) {
    	if(storedDBValues.containsKey(key)) {
    		Optional value = storedDBValues.get(key);
    		if(value.isPresent() && !(value.get() instanceof String) ) {
    			return Optional.empty();
    		}
			return value;
    	}
    	else	
    		System.err.println(this.toString() + " has no String " + key + ". Returning Optional.empty");
    	return Optional.empty();
    }
    
    public Set<String> getStoredDBValueIDs() {
    	return this.storedDBValues.keySet();
    }
  
    public void storeDBValue(String key, Optional value) {
        if (value == null || key == null)
            throw new IllegalArgumentException("Can't store DB Value because at least one parameter is null: ("+key+", "+value+")");
        storedDBValues.put(key, value);
    }   
    
    public <T> Optional<T> getStoredDBValue(String key) {
        Optional obj = Optional.empty();
        if (storedDBValues.containsKey(key)) {
            // Return cached if it exists
            obj = storedDBValues.get(key);
            if (obj != null) 
                return obj;
        } else {
        	System.err.println("No DB Value '"+key+"' is stored in this state. Returning Optional.empty.");
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
            throw new IllegalArgumentException("Can't store DB Value because at least one parameter is null: ("+key+", "+value+")");
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
        	System.err.println("No UserVariable result '"+key+"' is stored in this state. Returning Optional.empty.");
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
            throw new IllegalArgumentException("Can't store DB Value because at least one parameter is null: ("+key+", "+value+")");
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
        	System.err.println("No AssertionResult '"+key+"' is stored in this state. Returning Optional.empty.");
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
            return this.timestamp.equals(otherS.timestamp) 
            		&& this.storedDBValues.equals(otherS.storedDBValues)
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
    	return "DBValues=" + this.storedDBValues.toString() + " UserVariables=" + this.storedUserVariables.toString() + " Assertions=" + this.storedAssertions.toString();
    }
    
    public StateListHandler getStateListHandler() {
		return stateListHandler;
	}

	public void setStateListHandler(StateListHandler stateListHandler) {
		this.stateListHandler = stateListHandler;
	}
}