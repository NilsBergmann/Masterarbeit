package bergmann.masterarbeit.generationtarget.expressions;

import java.util.List;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_Finally extends UnaryExpression<Boolean, Boolean> {
    RelativeTimeInterval interval;

    public LTL_Finally(Expression<Boolean> expr) {
        this(expr, null);
    }

    public LTL_Finally(Expression<Boolean> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        List<State> relevantStates = null;
        boolean hasInterval = this.interval != null;
        boolean realTime = state.dataController.isRealTime();

        // Get relevant states
        if (hasInterval) {
            AbsoluteTimeInterval relevantTime = this.interval.addInstant(state.timestamp);
            relevantStates = state.dataController.getStatesInInterval(relevantTime);
        } else {
            relevantStates = state.dataController.getAllStatesAfter(state);
        }

        // Check if any state evaluates to true
        for (State current : relevantStates) {
            Optional<Boolean> result = expr.evaluate(current);
            if (result.isPresent() && result.get() == true)
                return Optional.of(true);
        }
        // No matching state found

        // Handle different evaluation cases
        if (hasInterval) {
            // Expr has interval (e.g F[0s, 20s](X))
            if (realTime) {
                // Realtime mode -> There might be future states coming
                if (state.dataController.intervalIsInRange(this.interval.addInstant(state.timestamp))) {
                    // Interval is completely inside timeframe of known data when evaluating
                    return Optional.of(false);
                } else {
                    // There might be some future state where X evaluates to true
                    return Optional.empty();
                }
            } else {
                // Non realtime mode -> No more future states coming
                return Optional.of(false);
            }
        } else {
            // Expr doesnt have interval
            if (realTime) {
                // Realtime mode -> A future states might cause an evaluation to true
                return Optional.empty();
            } else {
                // Non realtime mode -> No more future states coming
                return Optional.of(false);
            }
        }
    }
}