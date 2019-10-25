package bergmann.masterarbeit.generationtarget.dataaccess;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class DatabaseWrapper {
    public static String TIMESTAMP_COLUMN_NAME = "Timestamp";

    public Map<String, Unit> numberColumns;
    public List<String> booleanColumns;
    public List<String> stringColumns;

    public Connection conn;
    public String tableName = null;

    public DatabaseWrapper() {
        this.numberColumns = new HashMap<String, Unit>();
        this.booleanColumns = new ArrayList<>();
        this.stringColumns = new ArrayList<>();
    }

    public DatabaseWrapper(String databaseName) {
        this();
    }

    public DatabaseWrapper(String databaseName, String tableName) {
        this(databaseName);
        this.setTable(tableName);
    }

    public boolean isConnected() {
        return this.conn != null;
    }

    public Connection connect(String databaseName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + databaseName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("SUCCESS: Connected to database '" + databaseName + "'");
        } catch (SQLException e) {
            conn = null;
            System.err.println("ERROR: " + e.getMessage());
        }
        this.conn = conn;
        return conn;
    }

    public void disconnect() {
        try {
            this.conn.close();
        } catch (Exception e) {
            System.err.println("ERROR when disconnecting: " + e);
        }
        this.conn = null;
        this.tableName = null;
    }
    
    public List<State> getStates(){
    	return this.getStatesFromSQL("SELECT * FROM " + this.tableName);
    }

    public List<State> getStatesAfter(Instant timestamp){
    	String sql = "SELECT * FROM " + this.tableName + " WHERE " + TIMESTAMP_COLUMN_NAME + " > "+  timestamp.toEpochMilli();
    	return this.getStatesFromSQL(sql);
    }
    
    private List<State> getStatesFromSQL(String sql) {
        if (tableName == null)
            return null;
        List<State> states = new ArrayList<State>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                long timestamp = rs.getLong(TIMESTAMP_COLUMN_NAME);
                State current = new State(Instant.ofEpochMilli(timestamp));
                for (String name : booleanColumns) {
                    try {
                        boolean x = rs.getBoolean(name);
                        Optional<Boolean> data = rs.wasNull() ? Optional.empty() : Optional.of(x);
                        current.storeDBValue(name, data);
                    } catch (SQLException e) {
                        System.err.println("Couldnt get boolean '" + name + "' for " + current.toString()
                                + ". Saving Optional.empty " + e);
                        current.storeDBValue(name, Optional.empty());
                    }
                }
                for (String name : stringColumns) {
                    try {
                        String x = rs.getString(name);
                        Optional<String> data = rs.wasNull() ? Optional.empty() : Optional.of(x);
                        current.storeDBValue(name, data);
                    } catch (SQLException e) {
                        System.err.println("Couldnt get boolean '" + name + "' for " + current.toString()
                                + ". Saving Optional.empty " + e);
                        current.storeDBValue(name, Optional.empty());
                    }
                }
                for (Entry<String, Unit> pair : numberColumns.entrySet()) {
                    Unit unit = pair.getValue();
                    String name = pair.getKey();
                    try {
                        double value = rs.getDouble(name);
                        Amount x = Amount.valueOf(value, unit);
                        Optional<Amount> data = rs.wasNull() ? Optional.empty() : Optional.of(x);
                        current.storeDBValue(name, data);
                    } catch (SQLException e) {
                        System.err.println("Couldnt get boolean '" + name + "' for " + current.toString()
                                + ". Saving Optional.empty " + e);
                        current.storeDBValue(name, Optional.empty());
                    }
                }
                states.add(current);
            }
        } catch (Exception e) {
            System.err.println("Getting states from " + tableName + " failed: " + e.toString());
            return new ArrayList<State>();
        }
        return states;
    }

    public List<String> getTables() {
        List<String> names = new ArrayList<String>();
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                names.add(rs.getString(3));
            }
        } catch (Exception e) {
            System.err.println("ERROR " + e);
        }
        return names;
    }

    public void setTable(String name) {
        if (this.getTables().contains(name))
            this.tableName = name;
        else
            throw new IllegalArgumentException("No table with name " + name);
    }

    public void registerNumberColumn(String name, Unit unit) {
        if (isNotRegistered(name))
            this.numberColumns.put(name, unit);
        else
            System.err.println("Column " + name + " is already registered");
    }

    public void registerStringColumn(String name) {
        if (isNotRegistered(name))
            this.stringColumns.add(name);
        else
            System.err.println("Column " + name + " is already registered");
    }

    public void registerBooleanColumn(String name) {
        if (isNotRegistered(name))
            this.booleanColumns.add(name);
        else
            System.err.println("Column " + name + " is already registered");
    }

    private boolean isNotRegistered(String columnName) {
        return !numberColumns.containsKey(columnName) && !stringColumns.contains(columnName)
                && !booleanColumns.contains(columnName) && !columnName.equals(TIMESTAMP_COLUMN_NAME);
    }
}