package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.DatabaseWrapper;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class StringDatabaseAccess extends Expression<String> {
    public String columnName;

    public StringDatabaseAccess(String columnName) {
        super();
        this.columnName = columnName;
    }

    public Optional<String> evaluate(State state) {
        Optional<String> retVal = state.getDBString(this.columnName);
        return retVal;
    }
    public String toString() {
    	return "DB_STRING[" + columnName + "]";
    }
}