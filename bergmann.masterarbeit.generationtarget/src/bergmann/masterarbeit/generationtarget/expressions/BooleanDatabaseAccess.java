package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class BooleanDatabaseAccess extends Expression<Boolean> {
    public String columnName;

    public BooleanDatabaseAccess(String columnName) {
        this.columnName = columnName;
    }

    public Optional<Boolean> evaluate(State state) {
        Optional<Boolean> retVal = state.getDBBoolean(this.columnName);
        return retVal;
    }
    
    public String toString() {
    	return "DB_BOOL[" + columnName + "]";
    }
}