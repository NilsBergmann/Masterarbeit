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
        // φ W ψ ≡ (φ U ψ) ∨ G φ ≡ φ U (ψ ∨ G φ) ≡ ψ R (ψ ∨ φ)
        // φ W ψ ≡ ψ R (ψ ∨ φ)
        Expression<Boolean> leftOrRight = new Or(left, right);
        helper = new LTL_Release(right, leftOrRight, interval);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        return helper.evaluate(state);
    }
    
    public String toString() {
    	return "W"+"("+left+","+right+")";
    }
}