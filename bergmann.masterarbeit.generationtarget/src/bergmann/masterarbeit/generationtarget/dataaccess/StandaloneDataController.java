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

public class StandaloneDataController {
    private DatabaseWrapper dbWrapper;
    public StateListHandler stateHandler;
    private boolean isRealTime = false;

    public void setRealTime(boolean isRealTime) {
		this.isRealTime = isRealTime;
	}

	public StandaloneDataController(boolean isRealTime) {
    	stateHandler = new StateListHandler(isRealTime);
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
        List<State> readStates = this.dbWrapper.getStates();
        for (State state : readStates) {
			if(!stateHandler.contains(state)) {
				stateHandler.add(state);				
			}
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
        
        for (State state : this.stateHandler.getAllStates()) {
        	System.out.println("------------------");
            System.out.println("State " + state.toString()+"\n");
            for (UserVariable userVariable : userVars) {
                Optional result = userVariable.evaluate(state);
                System.out.println("UserVar: " + userVariable.name + " -> " + result);
            }
            System.out.println("");
            for (Assertion assertion : assertions) {
                Optional result = assertion.evaluate(state);
                System.out.println("Assertion: " + assertion.name + " -> " + result);
            }
            System.out.println("");
        }
        System.out.println("Evaluation complete!");
        writeToCSV("tableName", this.stateHandler.getAllStates());
    }
    
    private void writeToCSV(String path, List<State> states) {
    	System.out.println("Writing data to " + path);
    	Set<String> storedValues = new LinkedHashSet<String>();
    	/*
    	// TODO: Fix
    	for (State state : states) {
			storedValues.addAll(this.stateHandler.getStoredKeys());
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
    	*/
    }
    
}