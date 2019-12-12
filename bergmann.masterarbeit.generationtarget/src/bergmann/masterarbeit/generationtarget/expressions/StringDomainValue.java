package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class StringDomainValue extends Expression<String> {
    public String columnName;

    public StringDomainValue(String columnName) {
        super();
        this.columnName = columnName;
    }

    public Optional<String> evaluate(State state) {
        Optional<String> retVal = state.getDomainString(this.columnName);
        return retVal;
    }

    public String toString() {
        return "Domain_STRING[" + columnName + "]";
    }
}