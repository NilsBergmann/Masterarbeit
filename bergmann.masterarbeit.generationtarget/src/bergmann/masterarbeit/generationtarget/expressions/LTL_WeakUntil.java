package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_WeakUntil extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;

    public LTL_WeakUntil(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    public LTL_WeakUntil(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        // TODO: Implement this
        return Optional.empty();
    }
}