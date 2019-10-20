package bergmann.masterarbeit.generationtarget.expressions;

import java.util.List;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_Until extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;
    Expression<Boolean> helper;

    public LTL_Until(Expression<Boolean> left, Expression<Boolean> right) {
        this(left, right, null);
    }

    public LTL_Until(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
        // not (a U b) = (not a R not b)
        // -> a U b = not((not a) R (not b))
        helper = new BoolNegation(new LTL_Release(new BoolNegation(left), new BoolNegation(right), interval));
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        return helper.evaluate(state);
    }

    public String toString() {
        return "U" + "(" + left + "," + right + ")";
    }
}