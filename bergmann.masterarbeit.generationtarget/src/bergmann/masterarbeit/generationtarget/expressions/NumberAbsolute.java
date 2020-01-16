package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class NumberAbsolute extends UnaryExpression<Amount, Amount> {

	public NumberAbsolute(Expression<Amount> expr) {
		super(expr);
	}

	@Override
	public Optional<Amount> evaluate(State state) {
		Optional<Amount> eval = expr.evaluate(state);
		if(!eval.isPresent())
			return Optional.empty();
		else {
			Amount x = eval.get();
			return Optional.of(x.abs());
		}
	}


}
