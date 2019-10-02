package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class AggregateAverage extends UnaryExpression<Amount, Amount> {
    RelativeTimeInterval interval;

    public AggregateAverage(Expression<Amount> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
    }

    @Override
    public Optional<Amount> evaluate(State state, DataController dataSource) {
        // TODO: Implement this
        return Optional.empty();
    }
}