package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberDomainValue extends Expression<Amount> {
    public String columnName;

    public NumberDomainValue(String columnName) {
        super();
        this.columnName = columnName;
    }

    public Optional<Amount> evaluate(State state) {
        Optional<Amount> retVal = state.getDomainAmount(this.columnName);
        return retVal;
    }

    public String toString() {
        return "Domain_AMOUNT[" + columnName + "]";
    }
}