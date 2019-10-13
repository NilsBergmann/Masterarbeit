package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class BoolDatabaseAccess extends Expression<Boolean> {
    public String columnName;

    public BoolDatabaseAccess(String columnName) {
        this.columnName = columnName;
    }

    public Optional<Boolean> evaluate(State state) {
        Optional<Boolean> retVal = state.getDBBoolean(this.columnName);
        return retVal;
    }
}