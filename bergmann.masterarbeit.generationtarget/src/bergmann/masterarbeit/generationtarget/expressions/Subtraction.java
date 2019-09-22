package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Subtraction extends BinaryExpression<Double, Double, Double> {

    public Subtraction(Expression<Double> left, Expression<Double> right) {
        super(left, right);
    }

    @Override
    public Optional<Double> evaluate(State state, DataController dataSource) {
        Optional<Double> a = left.evaluate(state, dataSource);
        Optional<Double> b = right.evaluate(state, dataSource);
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        else {
            return Optional.of(a.get() - b.get());
        }
    }
}