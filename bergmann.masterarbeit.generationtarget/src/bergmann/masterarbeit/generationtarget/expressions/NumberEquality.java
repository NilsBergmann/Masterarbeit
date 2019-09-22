package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberEquality extends BinaryExpression<Double, Double, Boolean> {
    String operator = null;

    public NumberEquality(Expression<Double> left, Expression<Double> right, String operator) {
        super(left, right);
        this.operator = operator;
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        Optional<Double> a = this.left.evaluate(state, dataSource);
        Optional<Double> b = this.left.evaluate(state, dataSource);
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        else
            switch (this.operator) {
            case ">":
                return Optional.of(a.get() > b.get());
            case ">=":
                return Optional.of(a.get() >= b.get());
            case "<":
                return Optional.of(a.get() < b.get());
            case "<=":
                return Optional.of(a.get() <= b.get());
            case "==":
                return Optional.of(a.get() == b.get());
            case "!=":
                return Optional.of(a.get() != b.get());
            default: {
                System.out.println("ERROR: Unexpected operator '" + this.operator + "'");
                return Optional.empty();
            }
            }
    }

}