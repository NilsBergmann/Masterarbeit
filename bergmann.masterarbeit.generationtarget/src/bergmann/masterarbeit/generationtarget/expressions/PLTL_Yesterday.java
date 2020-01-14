package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Yesterday extends UnaryExpression<Boolean, Boolean> {
	RelativeTimeInterval interval;
	
    public PLTL_Yesterday(Expression<Boolean> expr) {
    	this(expr, null);
    }
    
    public PLTL_Yesterday(Expression<Boolean> expr, RelativeTimeInterval interval) {
    	super(expr);
    	this.interval = interval;
    }
    @Override
    public Optional<Boolean> evaluate(State state) {
        State previous = state.stateListHandler.getPreviousState(state);
        if (previous == null) {
            // The Z operator is similar to the Y operator, and it only differs in the way
            // the initial time instant is dealt with: at time zero, Y(x) is false, while Z(x)
            // is true. [Bounded Verification of Past LTL]
            return Optional.of(false);
        }
        if(this.interval != null) {
        	AbsoluteTimeInterval absInterval = this.interval.addInstant(state.timestamp);
        	if(!absInterval.contains(previous.timestamp))
        		return Optional.of(false);
        }
        return expr.evaluate(previous);
    }

    public String toString() {
        return "Y" + "(" + expr + ")";
    }
}