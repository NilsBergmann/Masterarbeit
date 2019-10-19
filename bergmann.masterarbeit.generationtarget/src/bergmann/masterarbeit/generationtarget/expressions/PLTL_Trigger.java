package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Trigger extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;
    Expression<Boolean> helper;

    public PLTL_Trigger(Expression<Boolean> left, Expression<Boolean> right) {
        this(left, right, null);
    }

    public PLTL_Trigger(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
        // x T y = not ((not x) S (not ψ)) [Bounded Verification of Past LTL]
        Expression<Boolean> notLeft = new BoolNegation(left);
        Expression<Boolean> notRight = new BoolNegation(right);
        helper = new BoolNegation(new PLTL_Since(notLeft, notRight, interval));
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        return helper.evaluate(state);
    }

    public String toString() {
        return "T" + "(" + left + "," + right + ")";
    }
}