package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class UserVariable<T> extends Expression<T> {
    public Expression<T> expression;
    public String name;

    public UserVariable(String name, Expression<T> expr) {
        this.expression = expr;
        this.name = name;
    }

    public Optional<T> evaluate(State state, DataController dataSource) {
        return expression.evaluate(state, dataSource);
    }
}