package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberLiteral implements Expression<Double> {
    double value;

    public NumberLiteral(double value) {
        super();
        this.value = value;
    }

    public Optional<Double> evaluate(State state, DataController dataSource) {
        return Optional.of(this.value);
    }
}