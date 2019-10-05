package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class LTL_Next extends UnaryExpression<Boolean, Boolean> {

    public LTL_Next(Expression<Boolean> expr) {
        super(expr);
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        State next = dataSource.getFollowingState(state);
        if (next == null) {
            return Optional.empty();
        }
        return expr.evaluate(next, dataSource);
    }
}