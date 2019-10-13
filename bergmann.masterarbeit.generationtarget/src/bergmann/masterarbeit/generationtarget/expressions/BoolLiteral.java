package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class BoolLiteral extends Expression<Boolean> {
    boolean value;

    public BoolLiteral(boolean value) {
        super();
        this.value = value;
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        return Optional.of(this.value);
    }
}