package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NotEquals extends BinaryExpression<Object, Object, Boolean> {
    String operator = null;

    public NotEquals(Expression<Object> left, Expression<Object> right) {
        super(left, right);
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        Optional<Object> a = this.left.evaluate(state, dataSource);
        Optional<Object> b = this.right.evaluate(state, dataSource);
        // One is missing: Return Nothing
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        if (a.get() == null || b.get() == null)
            return Optional.empty();
        boolean eq = a.get().equals(b.get());
        return Optional.of(!eq);
    }

}