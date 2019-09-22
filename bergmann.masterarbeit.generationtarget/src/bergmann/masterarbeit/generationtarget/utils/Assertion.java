package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Assertion {
    public Expression<Boolean> expression;
    public DataController dataSource;

    public Assertion(Expression<Boolean> expr, DataController dataSource) {
        this.expression = expr;
        this.dataSource = dataSource;
    }

    public Optional<Boolean> evaluateAt(State state) {
        return expression.evaluate(state, this.dataSource);
    }

    public void setDataSource(DataController dataSource) {
        this.dataSource = dataSource;
    }

    public void setExpression(Expression<Boolean> expr) {
        this.expression = expr;
    }
}