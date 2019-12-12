package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Z extends UnaryExpression<Boolean, Boolean> {
    RelativeTimeInterval interval;
    Expression<Boolean> helper;

    public PLTL_Z(Expression<Boolean> expr) {
    	this(expr, null);
    }
    
    public PLTL_Z(Expression<Boolean> expr, RelativeTimeInterval interval) {
    	super(expr);
    	this.interval = interval;
    }
    
    @Override
    public Optional<Boolean> evaluate(State state) {
        State previous = state.stateListHandler.getPreviousState(state);
        if (previous == null) {
            // The Z operator is similar to the Y operator, and it only differs in the way
            // the initial time instant is dealt with: at time zero, Yφ is false, while Zφ
            // is true. [Bounded Verification of Past LTL]
            return Optional.of(true);
        }
        if(this.interval != null) {
        	AbsoluteTimeInterval absInterval = this.interval.addInstant(state.timestamp);
        	if(!absInterval.contains(previous.timestamp))
        		return Optional.empty();
        }
        return expr.evaluate(previous);
    }

    public String toString() {
        return "Z" + "(" + expr + ")";
    }
}