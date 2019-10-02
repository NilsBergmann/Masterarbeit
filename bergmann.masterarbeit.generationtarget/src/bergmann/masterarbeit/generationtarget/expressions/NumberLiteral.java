package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberLiteral implements Expression<Amount> {
    Amount value;

    public NumberLiteral(long value, Unit unit) {
        super();
        this.value = Amount.valueOf(value, unit);
    }

    public Optional<Amount> evaluate(State state, DataController dataSource) {
        return Optional.of(this.value);
    }
}