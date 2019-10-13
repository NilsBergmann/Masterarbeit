package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.converter.ConversionException;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberInequality extends BinaryExpression<Amount, Amount, Boolean> {
    String operator = null;

    public NumberInequality(Expression<Amount> left, Expression<Amount> right, String operator) {
        super(left, right);
        this.operator = operator;
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        Optional<Amount> a = this.left.evaluate(state);
        Optional<Amount> b = this.right.evaluate(state);
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        if (a.get() == null || b.get() == null)
            return Optional.empty();
        else {
            Amount aV = a.get();
            Amount bV = b.get();
            try {
                int comparison = aV.compareTo(bV);
                switch (this.operator) {
                case ">":
                    return Optional.of(comparison > 0);
                case ">=":
                    return Optional.of(comparison >= 0);
                case "<":
                    return Optional.of(comparison < 0);
                case "<=":
                    return Optional.of(comparison <= 0);
                case "==":
                    return Optional.of(comparison == 0);
                case "!=":
                    return Optional.of(comparison != 0);
                default: {
                    System.out.println("ERROR: Unexpected operator '" + this.operator + "'");
                    return Optional.empty();
                }
                }
            } catch (ConversionException e) {
                System.err.println("Inequality: Incompatible units: " + aV.getUnit() + " and " + bV.getUnit());
                return Optional.empty();
            }

        }
    }
}
