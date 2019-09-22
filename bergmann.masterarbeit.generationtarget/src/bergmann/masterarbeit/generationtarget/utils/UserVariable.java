package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class UserVariable<T> implements Expression<T> {
    public Expression<T> expression;

    public UserVariable(Expression<T> expr) {
        this.expression = expr;
    }

    public Optional<T> evaluate(State state, DataController dataSource) {
        return expression.evaluate(state, dataSource);
    }
    // TODO: Add Logging
}