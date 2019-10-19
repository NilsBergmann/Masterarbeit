package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.converter.ConversionException;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Multiplication extends BinaryExpression<Amount, Amount, Amount> {

    public Multiplication(Expression<Amount> left, Expression<Amount> right) {
        super(left, right);
    }

    @Override
    public Optional<Amount> evaluate(State state) {
        Optional<Amount> a = left.evaluate(state);
        Optional<Amount> b = right.evaluate(state);
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        else {
            Amount aV = a.get();
            Amount bV = b.get();
            try {
                Amount sum = aV.times(bV);
                return Optional.of(sum);
            } catch (ConversionException e) {
                System.err.println("Incompatible units: " + aV.getUnit() + " and " + bV.getUnit());
                return Optional.empty();
            }
        }
    }
    
    public String toString() {
    	return "(" + left + " * "+ right + ") ";
    }
}