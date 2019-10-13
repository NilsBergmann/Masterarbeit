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
    public Optional<Boolean> evaluate(State state) {
    	if (state == null)
    		System.out.println("state is null");
        State next = state.dataController.getFollowingState(state);
        if (next == null) {
            return Optional.empty();
        }
        return expr.evaluate(next);
    }
}