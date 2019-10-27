package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class NumberNegation extends UnaryExpression<Amount, Amount> {

    public NumberNegation(Expression<Amount> expr) {
        super(expr);
    }

    @Override
    public Optional<Amount> evaluate(State state) {
        Optional<Amount> eval = this.expr.evaluate(state);
        if (!eval.isPresent())
            return Optional.empty();
        else
            return Optional.of(eval.get().times(-1));
    }
    
    @Override
    public String toString() {
    	return "-" + expr.toString();
    }
}