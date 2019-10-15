package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class OffsetByStates<T extends Object> extends UnaryExpression<T, T>{
	Expression<T> expr;
	int amount = 0;
	
	public OffsetByStates(Expression<T> expr, int amount) {
		super(expr);
		this.expr = expr;
		this.amount = amount;
	}

	@Override
	public Optional<T> evaluate(State state) {
		State target = state.dataController.getStateOffsetBy(state, this.amount);
		if(target == null)
			return Optional.empty();
		else
			return this.expr.evaluate(target);
	}
	
    @Override
    public String toString() {
    	return "OFFSET["+ amount +"states](" + expr.toString()+")";
    }

}
