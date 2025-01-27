package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class BoolNegation extends UnaryExpression<Boolean, Boolean> {

    public BoolNegation(Expression<Boolean> expr) {
        super(expr);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        Optional<Boolean> eval = this.expr.evaluate(state);
        if (!eval.isPresent())
            return Optional.empty();
        else
            return Optional.of(!eval.get());
    }
    
    public String toString() {
    	return "(NOT " + expr + ")";
    }

}