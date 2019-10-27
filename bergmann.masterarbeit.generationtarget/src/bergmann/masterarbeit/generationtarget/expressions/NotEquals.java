package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NotEquals<A extends Object, B extends Object> extends BinaryExpression<A, B, Boolean> {
    String operator = null;
    Expression helper;

    public NotEquals(Expression<A> left, Expression<B> right) {
        super(left, right);
        helper = new BoolNegation(new Equals(left, right));
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
    	return helper.evaluate(state);
    }

    public String toString() {
    	return "(" + left + " != "+ right + ") ";
    }
}