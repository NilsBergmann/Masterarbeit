package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class NumberRoot extends UnaryExpression<Amount, Amount> {
	int nThRoot;
	public NumberRoot(Expression<Amount> expr, int nThRoot) {
		super(expr);
		this.nThRoot = nThRoot;
	}

	@Override
	public Optional<Amount> evaluate(State state) {
		Optional<Amount> eval = expr.evaluate(state);
		if(!eval.isPresent())
			return Optional.empty();
		else {
			Amount x = eval.get();
			try {
				Amount result = x.root(nThRoot);	
				return Optional.of(result);
			} catch (ArithmeticException e) {
				return Optional.empty();
			}
		}
	}


}
