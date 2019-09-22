package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.DatabaseWrapper;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class BoolDatabaseAccess implements Expression<Boolean> {
    public String tableName, columnName;

    public BoolDatabaseAccess(String tableName, String columnName) {
        this.columnName = columnName;
        this.tableName = tableName;
    }

    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        DatabaseWrapper dbWrap = dataSource.getDatabaseWrapper();
        Optional<Boolean> retVal = dbWrap.getBoolean(state, this.tableName, this.columnName);
        return retVal;
    }
}