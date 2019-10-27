package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberLiteral extends Expression<Amount> {
    Amount value;

    public NumberLiteral(double value, Unit unit) {
        super();
        this.value = Amount.valueOf(value, unit);
    }

    public Optional<Amount> evaluate(State state) {
        return Optional.of(this.value);
    }
    
    @Override
    public String toString() {
    	return value.toString();
    }
}