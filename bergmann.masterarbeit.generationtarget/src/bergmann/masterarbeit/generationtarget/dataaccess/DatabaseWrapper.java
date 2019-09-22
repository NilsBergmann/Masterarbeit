package bergmann.masterarbeit.generationtarget.dataaccess;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.print.event.PrintJobListener;

public class DatabaseWrapper {
    public static String TIMESTAMP_COLUMN_NAME = "Timestamp";
    public Connection conn;

    public DatabaseWrapper(String databaseName) {
        conn = connect(databaseName);
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
        return conn;
    }

    public Optional<Boolean> getBoolean(State state, String tableName, String columnName) {
        // TODO: Implement this
        return Optional.empty();
    }

    public Optional<Double> getNumber(State state, String tableName, String columnName) {
        // TODO: Implement this
        return Optional.empty();
    }

    public Optional<String> getString(State state, String tableName, String columnName) {
        // TODO: Implement this
        return Optional.empty();
    }

    private ResultSet getValue(State state, String tableName, String columnName) {
        Timestamp timestamp = Timestamp.from(state.timestamp);
        long timeInMillisec = timestamp.getTime();
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE " + TIMESTAMP_COLUMN_NAME + " = "
                + timeInMillisec;
        try {
            Statement stmnt = conn.createStatement();
            stmnt.execute(sql);
            ResultSet result = stmnt.getResultSet();
            return result;
        } catch (Exception e) {
            // TODO Handle this
        }
        return null;
    }

    public List<State> getStatesFrom(String tableName) {
        List<State> states = new ArrayList<State>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + tableName);
            while (rs.next()) {
                long timestamp = rs.getLong(TIMESTAMP_COLUMN_NAME);
                State current = new State(Instant.ofEpochMilli(timestamp));
                states.add(current);
            }
        } catch (Exception e) {
            // TODO: handle exception
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
            System.out.println("ERROR " + e);
        }
        return names;
    }

    public static void main(String[] args) {
        DatabaseWrapper dbWrap = new DatabaseWrapper("test.db");
        System.out.println(dbWrap.getTables());
        List<State> states = dbWrap.getStatesFrom("test");
        for (State state : states) {
            System.out.println("State with timestamp:" + state.timestamp);
        }

    }

}