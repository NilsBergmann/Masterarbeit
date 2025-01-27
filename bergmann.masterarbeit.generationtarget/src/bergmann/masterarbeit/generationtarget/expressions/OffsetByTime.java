package bergmann.masterarbeit.generationtarget.expressions;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;

public class OffsetByTime<T extends Object> extends UnaryExpression<T, T> {
	Expression<T> expr;
	Duration amount;

	public OffsetByTime(Expression<T> expr, Duration amount) {
		super(expr);
		if (amount == null)
			throw new IllegalArgumentException("null is not a valid offset");
		this.amount = amount;
		this.expr = expr;
	}

	@Override
	public Optional<T> evaluate(State state) {
		Instant targetTimestamp = state.timestamp.plus(amount);
		State target = state.stateListHandler.getClosestState(targetTimestamp);
		if (target == null)
			return Optional.empty();
		return this.expr.evaluate(target);
	}

	@Override
	public String toString() {
		return "OFFSET[" + amount.toMillis() + " ms](" + expr.toString() + ")";
	}
}
