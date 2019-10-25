package bergmann.masterarbeit.generationtarget.dataaccess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import javax.measure.unit.Unit;

import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.Assertion;
import bergmann.masterarbeit.generationtarget.utils.MonitorDeclaration;
import bergmann.masterarbeit.generationtarget.utils.UIUtils;
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

    public List<State> updateStates() {
    	List<State> readStates = new ArrayList<State>();
    	if(this.stateHandler.isEmpty())
    		readStates = this.dbWrapper.getStates();
    	else
    		readStates = this.dbWrapper.getStatesAfter(this.stateHandler.getLatestState().timestamp);
        for (State state : readStates) {
			if(!stateHandler.contains(state)) {
				stateHandler.add(state);				
			}
		}
        return readStates;
    }

    public void selectTable(String tablename) {
        if (this.dbWrapper != null && this.dbWrapper.isConnected()) {
            try {
                this.dbWrapper.setTable(tablename);
                this.stateHandler = new StateListHandler(isRealTime);
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
    	System.out.println("Registered required DB Number: " + name + "["+unit+"]");
        this.dbWrapper.registerNumberColumn(name, unit);
    }

    public void registerStringDBColumn(String name) {
    	System.out.println("Registered required DB String: " + name );
        this.dbWrapper.registerStringColumn(name);
    }

    public void registerBooleanDBColumn(String name) {
    	System.out.println("Registered required DB Boolean: " + name );
        this.dbWrapper.registerBooleanColumn(name);
    }
    
    public void registerRequiredData(MonitorDeclaration monitors) {
    	for (String id : monitors.getRequiredDataStrings()) {
			this.registerStringDBColumn(id);
		}
    	for (String id : monitors.getRequiredDataBooleans()) {
			this.registerBooleanDBColumn(id);
		}
    	for (Map.Entry<String, Unit> entry : monitors.getRequiredDataNumbers().entrySet()) {
			String id = entry.getKey();
			Unit unit = entry.getValue();
			this.registerNumberDBColumn(id, unit);
		}
    }

    public void runEvaluation(MonitorDeclaration monitors, String tableName) {
        if (this.isRealTime) {
            System.err.println("real time not implemented yet");
            // TODO: Implement real time mode
        } else {
            this.runNonRealtimeEvaluation(monitors, tableName);
        }
    }

    private void runNonRealtimeEvaluation(MonitorDeclaration monitors, String tableName) {
        File folder = UIUtils.folderSelection();
    	this.registerRequiredData(monitors); 
        this.dbWrapper.setTable(tableName);
        // Get states
        this.updateStates();
        // Evaluate States
        System.out.println("Running evaluations...\n");
       
        for (State state : this.stateHandler.getAllStates()) {
        	System.out.println("Evaluating: " + state);
        	Map<String, Optional> result = monitors.evaluateAllAt(state);
        }
        System.out.println("Evaluation complete!");
        if(folder != null && folder.isDirectory())
        	writeToCSV(folder.toString(), tableName, this.stateHandler.getAllStates(), monitors);
    }
    
    private void writeToCSV(String mainFolder, String tableName, List<State> states, MonitorDeclaration monitors) {
    	
    	String path = mainFolder + "/" + monitors.getName() + "/";
    	File dir = new File(path);
    	dir.mkdirs();
    	File everything = new File(path +tableName+ ".csv");
    	File onlyAssertions = new File(path + tableName+ "_OnlyAssertions.csv");
    	System.out.println("Writing data to " + path);
    	
    	Set<String> storedDBValues = new LinkedHashSet<String>();
    	storedDBValues.addAll(monitors.getRequiredDataBooleans());
    	storedDBValues.addAll(monitors.getRequiredDataStrings());
    	storedDBValues.addAll(monitors.getRequiredDataNumbers().keySet());
    	Set<String> storedUserVars = new LinkedHashSet<String>(monitors.getDeclaredUserVariableNames());
    	Set<String> storedAssertions = new LinkedHashSet<String>(monitors.getDeclaredAssertionNames());
    	
    	try {
    	// Write everything.csv
   		FileWriter fw = new FileWriter(everything);
   		
    	// Create header
    	String header = "Timestamp; ";
    	for (String string : storedDBValues) {
    		header += string +";";
    	}
    	for (String string : storedUserVars) {
    		header += string +";";
    	}
    	for (String string : storedAssertions) {
    		header += string +";";
    	}
    	header+="\n";
    	
    	// Write state data
    	fw.write(header);
    	for (State state : states) {
			String current = state.timestamp.toString()+ ";";
			for (String key : storedDBValues) {
				Optional data = state.getStoredDBValue(key);
				String dataString = data.isPresent() ? data.get().toString() : " ";
				current += dataString + ";";
			}
			for (String key : storedUserVars) {
				Optional data = state.getStoredUserVariableResult(key);
				String dataString = data.isPresent() ? data.get().toString() : " ";
				current += dataString + ";";
			}
			for (String key : storedAssertions) {
				Optional data = state.getAssertionResult(key);
				String dataString = data.isPresent() ? data.get().toString() : " ";
				current += dataString + ";";
			}
			current+="\n";
			fw.write(current);
		}
    	fw.close();
    	} catch (IOException e) {
    		System.out.println("Writing results to csv failed. " + e);
    	}
    	
    	try {
    	// Write _OnlyAsssertions.csv
   		FileWriter fw = new FileWriter(onlyAssertions);
   		
    	// Create header
    	String header = "Timestamp; ";
    	for (String string : storedAssertions) {
    		header += string +";";
    	}
    	header+="\n";
    	
    	// Write state data
    	fw.write(header);
    	for (State state : states) {
			String current = state.timestamp.toString()+ ";";
			for (String key : storedAssertions) {
				Optional data = state.getAssertionResult(key);
				String dataString = data.isPresent() ? data.get().toString() : " ";
				current += dataString + ";";
			}
			current+="\n";
			fw.write(current);
		}
    	fw.close();
    	} catch (IOException e) {
    		System.err.println("Writing results to csv failed. " + e);
    	}
    	
    }
    
}