package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.TernaryExpression;

public class IfThenElse<T extends Object> extends TernaryExpression<Boolean, T, T, T> {
	public IfThenElse(Expression<Boolean> condition, Expression<T> then, Expression<T> elze) {
		super(condition, then, elze);
		// TODO Auto-generated constructor stub
	}
	@Override
	public Optional<T> evaluate(State state) {
		Optional<Boolean> condition = this.a.evaluate(state);
		if(!condition.isPresent())
			return Optional.empty();
		else {
			if(condition.get().equals(true)) {
				return b.evaluate(state);
			} else {
				return c.evaluate(state);
			}
		}
	}

}
