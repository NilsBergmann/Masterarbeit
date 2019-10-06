package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Assertion {
    public Expression<Boolean> expression;
    public String name;

    public Assertion(String name, Expression<Boolean> expr) {
        this.expression = expr;
        this.name = name;
    }

    public Optional<Boolean> evaluateAt(State state, DataController dataSource) {
        return expression.evaluate(state, dataSource);
    }

    public void setExpression(Expression<Boolean> expr) {
        this.expression = expr;
    }
}