package bergmann.masterarbeit.generationtarget.dataaccess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.measure.unit.Unit;

import bergmann.masterarbeit.generationtarget.utils.MonitorDeclaration;
import bergmann.masterarbeit.generationtarget.utils.UIUtils;

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
		if (this.stateHandler.isEmpty())
			readStates = this.dbWrapper.getStates();
		else
			readStates = this.dbWrapper.getStatesAfter(this.stateHandler.getLatestState().timestamp);
		for (State state : readStates) {
			if (!stateHandler.contains(state)) {
				stateHandler.add(state);
			}
		}
		return readStates;
	}

	public void selectTable(String tablename) {
		if (this.isConnectedToDB()) {
			try {
				this.dbWrapper.setTable(tablename);
				this.stateHandler = new StateListHandler(isRealTime);
			} catch (IllegalArgumentException e) {
				throw e;
			}
		} else {
			System.err.println("No database connection");
		}
	}

	public List<String> getTables() {
		if (this.isConnectedToDB()) {
			return this.dbWrapper.getTables();
		}
		System.err.println("No database connection");
		return new ArrayList<String>();
	}

	public boolean isConnectedToDB() {
		return this.dbWrapper != null && this.dbWrapper.isConnected();
	}

	public boolean isRealTime() {
		return this.isRealTime;
	}


	public void registerRequiredData(MonitorDeclaration monitors) {
		this.dbWrapper.setMonitors(monitors);
	}

	public void runEvaluation(MonitorDeclaration monitors, String tableName) {
		List<String> storedDBValues = new ArrayList<String>();
		storedDBValues.addAll(monitors.getRequiredDataBooleans());
		storedDBValues.addAll(monitors.getRequiredDataStrings());
		storedDBValues.addAll(monitors.getRequiredDataNumbers().keySet());
		List<String> storedUserVars = new ArrayList<String>(monitors.getDeclaredUserVariableNames());
		List<String> storedAssertions = new ArrayList<String>(monitors.getDeclaredAssertionNames());

		// Select files and folders
		File folder = UIUtils.folderSelection();
		String path = folder.toString() + "/" + monitors.getName() + "/";
		File dir = new File(path);
		dir.mkdirs();
		// stuff for console output
		long timestamp = System.currentTimeMillis();
		int stateCounter = 0;
		this.registerRequiredData(monitors);
		this.dbWrapper.setTable(tableName);
		try {
			FileWriter everythingFW = new FileWriter(new File(path + tableName + ".csv"));
			FileWriter onlyAssertionsFW = new FileWriter(new File(path + tableName + "_OnlyAssertions.csv"));
			// Write csv headers
			this.writeHeader(everythingFW, storedDBValues, storedUserVars, storedAssertions);
			this.writeHeader(onlyAssertionsFW, null, null, storedAssertions);
			do {
				// Try getting states
				List<State> read = this.updateStates();
				// Evaluate States if they exist
				if (read != null && !read.isEmpty()) {
					timestamp = System.currentTimeMillis();
					System.out.println("Found " + read.size() + " states");
					for (State state : read) {
						stateCounter++;
						System.out.println("Evaluating State #" + stateCounter + ": " + state);
						Map<String, Optional> result = monitors.evaluateAllAt(state);
						this.writeCSVRow(everythingFW, state, storedDBValues, storedUserVars, storedAssertions);
						this.writeCSVRow(onlyAssertionsFW, state, null, null, storedAssertions);
					}
					System.out.println("Evaluation complete!");
					everythingFW.flush();
					onlyAssertionsFW.flush();
				} else if(isRealTime) {
					long delta = System.currentTimeMillis() - timestamp;
					System.out.print("\rWaiting for new states in " + tableName + " (current amount: "+stateCounter+"). time since last update: " + delta + "ms");
					Thread.sleep(5);
				}
			} while (isRealTime);
		} catch (Exception e) {
			System.err.println("Interrupted! " + e);
		}
		
	}

	private void writeHeader(FileWriter fw, List<String> selectedDBValues, List<String> selectedUserVariables,
			List<String> selectedAssertions) {
		try {
			String header = "Timestamp; ";
			if (selectedDBValues != null) {
				for (String string : selectedDBValues) {
					header += string + "; ";
				}
			}
			if (selectedUserVariables != null) {
				for (String string : selectedUserVariables) {
					header += string + "; ";
				}
			}
			if (selectedAssertions != null) {
				for (String string : selectedAssertions) {
					header += string + "; ";
				}
			}
			header += "\n";
			fw.write(header);
			fw.flush();
		} catch (Exception e) {
			System.err.println("Writing header to " + fw + "failed. " + e);
		}
	}

	private void writeCSVRow(FileWriter fw, State state, List<String> selectedDBValues, List<String> selectedUserVariables,
			List<String> selectedAssertions) {
		try {
			String line = state.timestamp.toString() + ";";
			if (selectedDBValues != null) {
				for (String key : selectedDBValues) {
					Optional data = state.getStoredDBValue(key);
					String dataString = data.isPresent() ? data.get().toString() : " ";
					line += dataString + "; ";
				}
			}
			if (selectedUserVariables != null) {
				for (String key : selectedUserVariables) {
					Optional data = state.getStoredUserVariableResult(key);
					String dataString = data.isPresent() ? data.get().toString() : " ";
					line += dataString + "; ";
				}
			}
			if (selectedAssertions != null) {
				for (String key : selectedAssertions) {
					Optional data = state.getAssertionResult(key);
					String dataString = data.isPresent() ? data.get().toString() : "  ";
					line += dataString + "; ";
				}
			}
			line += "\n";
			fw.write(line);
		} catch (IOException e) {
			System.err.println("Could not write to file " + fw + ". " + e);
		}

	}

}