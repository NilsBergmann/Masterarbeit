package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class BooleanDomainValue extends Expression<Boolean> {
    public String columnName;

    public BooleanDomainValue(String columnName) {
        this.columnName = columnName;
    }

    public Optional<Boolean> evaluate(State state) {
        Optional<Boolean> retVal = state.getDomainBoolean(this.columnName);
        return retVal;
    }

    public String toString() {
        return "Domain_BOOL[" + columnName + "]";
    }
}