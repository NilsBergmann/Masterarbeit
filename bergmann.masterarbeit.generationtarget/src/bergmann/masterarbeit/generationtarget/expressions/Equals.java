package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Equals<A extends Object, B extends Object> extends BinaryExpression<A, B, Boolean> {
    String operator = null;

    public Equals(Expression<A> left, Expression<B> right) {
        super(left, right);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        Optional<A> a = this.left.evaluate(state);
        Optional<B> b = this.right.evaluate(state);
        // One is missing: Return Nothing
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        if (a.get() == null || b.get() == null)
            return Optional.empty();
        boolean eq = a.get().equals(b.get());
        return Optional.of(eq);
    }

    public String toString() {
    	return "(" + left + " == "+ right + ") ";
    }
}