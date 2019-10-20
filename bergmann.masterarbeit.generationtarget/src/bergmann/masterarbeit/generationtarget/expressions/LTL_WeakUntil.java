package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_WeakUntil extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;
    Expression<Boolean> helper;

    public LTL_WeakUntil(Expression<Boolean> left, Expression<Boolean> right) {
        this(left, right, null);
    }

    public LTL_WeakUntil(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
        // a W b = (b R (a or b)
        helper = new LTL_Release(right, new Or(left, right), interval);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        return helper.evaluate(state);
    }
    
    public String toString() {
    	return "W"+"("+left+","+right+")";
    }
}