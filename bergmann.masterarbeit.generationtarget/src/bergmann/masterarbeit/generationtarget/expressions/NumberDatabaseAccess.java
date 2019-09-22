package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.DatabaseWrapper;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberDatabaseAccess implements Expression<Double> {
    public String tableName, columnName;

    public NumberDatabaseAccess(String tableName, String columnName) {
        this.columnName = columnName;
        this.tableName = tableName;
    }

    public Optional<Double> evaluate(State state, DataController dataSource) {
        DatabaseWrapper dbWrap = dataSource.getDatabaseWrapper();
        Optional<Double> retVal = dbWrap.getNumber(state, this.tableName, this.columnName);
        return retVal;
    }
}