package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.converter.ConversionException;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Addition extends BinaryExpression<Amount, Amount, Amount> {

    public Addition(Expression<Amount> left, Expression<Amount> right) {
        super(left, right);
    }

    @Override
    public Optional<Amount> evaluate(State state, DataController dataSource) {
        Optional<Amount> a = left.evaluate(state, dataSource);
        Optional<Amount> b = right.evaluate(state, dataSource);
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        else {
            Amount aV = a.get();
            Amount bV = b.get();
            try {
                Amount sum = aV.plus(bV);
                return Optional.of(sum);
            } catch (ConversionException e) {
                System.err.println("Addition: Incompatible units: " + aV.getUnit() + " and " + bV.getUnit());
                return Optional.empty();
            }
        }
    }
}