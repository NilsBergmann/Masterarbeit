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

import bergmann.masterarbeit.generationtarget.utils.MonitorDeclaration;

public class DatabaseWrapper {
    public static String TIMESTAMP_COLUMN_NAME = "Timestamp";

    private MonitorDeclaration monitors;

    public Connection conn;
    public String tableName = null;

    public DatabaseWrapper() {

    }

    public DatabaseWrapper(MonitorDeclaration monitors) {
        this();
        this.monitors = monitors;
    }

    public MonitorDeclaration getMonitors() {
        return monitors;
    }

    public void setMonitors(MonitorDeclaration monitors) {
        this.monitors = monitors;
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

    public List<State> getStates() {
        return this.getStatesFromSQL("SELECT * FROM " + this.tableName);
    }

    public List<State> getStatesAfter(Instant timestamp) {
        String sql = "SELECT * FROM " + this.tableName + " WHERE " + TIMESTAMP_COLUMN_NAME + " > "
                + timestamp.toEpochMilli();
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
                current.initUnknowns(this.monitors);
                for (String name : monitors.getRequiredDataBooleans()) {
                    try {
                        boolean x = rs.getBoolean(name);
                        Optional<Boolean> data = rs.wasNull() ? Optional.empty() : Optional.of(x);
                        current.storeDomainValue(name, data);
                    } catch (SQLException e) {
                    }
                }
                for (String name : monitors.getRequiredDataStrings()) {
                    try {
                        String x = rs.getString(name);
                        Optional<String> data = rs.wasNull() ? Optional.empty() : Optional.of(x);
                        current.storeDomainValue(name, data);
                    } catch (SQLException e) {
                    }
                }
                for (Entry<String, Unit> pair : monitors.getRequiredDataNumbers().entrySet()) {
                    Unit unit = pair.getValue();
                    String name = pair.getKey();
                    try {
                        double value = rs.getDouble(name);
                        Amount x = Amount.valueOf(value, unit);
                        Optional<Amount> data = rs.wasNull() ? Optional.empty() : Optional.of(x);
                        current.storeDomainValue(name, data);
                    } catch (SQLException e) {
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

}