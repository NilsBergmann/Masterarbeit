package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Historically extends UnaryExpression<Boolean, Boolean> {
    RelativeTimeInterval interval;

    public PLTL_Historically(Expression<Boolean> expr) {
        this(expr, null);
    }

    public PLTL_Historically(Expression<Boolean> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        List<State> relevantStates = null;
        boolean hasInterval = this.interval != null;
        boolean realTime = dataSource.isRealTime();

        // Get relevant states
        if (hasInterval) {
            AbsoluteTimeInterval relevantTime = this.interval.addInstant(state.timestamp);
            relevantStates = dataSource.getStatesInInterval(relevantTime);
        } else {
            relevantStates = dataSource.getAllStatesBefore(state);
        }
        // Switch order
        Collections.reverse(relevantStates);

        // Check if any state evaluates to false
        for (State current : relevantStates) {
            Optional<Boolean> result = expr.evaluate(current, dataSource);
            if (result.isPresent() && result.get() == false)
                return Optional.of(false);
        }
        // No conflicting state found

        return Optional.of(true);
    }
}