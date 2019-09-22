package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class NumberNegation extends UnaryExpression<Double, Double> {

    public NumberNegation(Expression<Double> expr) {
        super(expr);
    }

    @Override
    public Optional<Double> evaluate(State state, DataController dataSource) {
        Optional<Double> eval = this.expr.evaluate(state, dataSource);
        if (!eval.isPresent())
            return Optional.empty();
        else
            return Optional.of(-eval.get());
    }

}