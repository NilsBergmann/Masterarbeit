package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class BoolNegation extends UnaryExpression<Boolean, Boolean> {

    public BoolNegation(Expression<Boolean> expr) {
        super(expr);
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        Optional<Boolean> eval = this.expr.evaluate(state, dataSource);
        if (!eval.isPresent())
            return Optional.empty();
        else
            return Optional.of(!eval.get());
    }

}