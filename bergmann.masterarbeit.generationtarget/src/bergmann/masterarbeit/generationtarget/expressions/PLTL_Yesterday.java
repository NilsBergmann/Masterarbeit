package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class PLTL_Yesterday extends UnaryExpression<Boolean, Boolean> {

    public PLTL_Yesterday(Expression<Boolean> expr) {
        super(expr);
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        State previous = dataSource.getPreviousState(state);
        if (previous == null) {
            return Optional.empty();
        }
        return expr.evaluate(previous, dataSource);
    }
}