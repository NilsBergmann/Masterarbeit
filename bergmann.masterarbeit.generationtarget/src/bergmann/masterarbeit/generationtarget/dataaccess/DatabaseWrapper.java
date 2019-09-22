package bergmann.masterarbeit.generationtarget.dataaccess;

import java.sql.*;
import java.util.Optional;

public class DatabaseWrapper {
    public Connection dbCon;

    public DatabaseWrapper(String databaseName) {
        dbCon = connect(databaseName);
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

}