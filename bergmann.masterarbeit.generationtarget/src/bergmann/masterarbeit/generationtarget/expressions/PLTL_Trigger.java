package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Trigger extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;

    public PLTL_Trigger(Expression<Boolean> left, Expression<Boolean> right) {
        this(left, right, null);
    }

    public PLTL_Trigger(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        // TODO: Implement this
        return Optional.empty();
    }
}