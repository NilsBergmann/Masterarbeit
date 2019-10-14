package bergmann.masterarbeit.generationtarget.dataaccess;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class State implements Comparable<State> {
    public Instant timestamp;
    public DataController dataController;
    Map<String, Optional> storedData;

    public State(Instant timestamp, DataController ctrl) {
        this.timestamp = timestamp;
        this.dataController = ctrl;
        storedData = new HashMap<String, Optional>();
    }

    public State(Instant timestamp) {
        this(timestamp, null);
    }

    public Optional<Boolean> getDBBoolean(String key) {
    	if(storedData.containsKey(key))
    		return storedData.get(key);
    	else	
    		System.err.println(this.toString() + " has no boolean " + key);
    	return Optional.empty();
    }
    
    public Optional<Amount> getDBAmount(String key) {
    	if(storedData.containsKey(key))
    		return storedData.get(key);
    	else	
    		System.err.println(this.toString() + " has no Amount " + key);
    	return Optional.empty();
    }
    
    public Optional<String> getDBString(String key) {
    	if(storedData.containsKey(key))
    		return storedData.get(key);
    	else	
    		System.err.println(this.toString() + " has no String " + key);
    	return Optional.empty();
    }
    
  
    public void store(String key, Optional value) {
        if (value == null || key == null)
            return;
        storedData.put(key, value);
        this.getStored(null);
    }   
    
    public <T> Optional<T> getStored(String key) {
        Optional obj = Optional.empty();
        if (storedData.containsKey(key)) {
            // Return cached if it exists
            obj = storedData.get(key);
        }
        if (obj != null && obj.isPresent()) {
            return obj;
        }
        return Optional.empty();
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
    
    public String toString() {
    	return "State[" + timestamp + "]";
    }
    
    public String toLongString() {
    	return this.toString() + " -> " + this.StoredDataToString();
    }
    
    public String StoredDataToString() {
    	return this.storedData.toString();
    }
}