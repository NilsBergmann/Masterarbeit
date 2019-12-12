package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_Next extends UnaryExpression<Boolean, Boolean> {
	RelativeTimeInterval interval;
	
    public LTL_Next(Expression<Boolean> expr) {
        this(expr, null);
    }
    
    public LTL_Next(Expression<Boolean> expr, RelativeTimeInterval interval) {
    	super(expr);
    	this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        State next = state.stateListHandler.getFollowingState(state);
        if (next == null) {
            return Optional.empty();
        }
        if(this.interval != null) {
        	AbsoluteTimeInterval absInterval = this.interval.addInstant(state.timestamp);
        	if(!absInterval.contains(next.timestamp))
        		return Optional.empty();
        }
        return expr.evaluate(next);
    }
    public String toString() {
    	return "N"+"("+expr+")";
    }
}