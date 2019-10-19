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
    public Optional<Boolean> evaluate(State state) {
        List<State> relevantStates = null;
        boolean hasInterval = this.interval != null;
        boolean realTime = state.dataController.isRealTime();

        // Get relevant states
        if (hasInterval) {
            AbsoluteTimeInterval relevantTime = this.interval.addInstant(state.timestamp);
            relevantStates = state.dataController.getStatesInInterval(relevantTime);
        } else {
            relevantStates = state.dataController.getAllStatesBefore(state);
            relevantStates.add(state);
        }

        // Check if any state evaluates to false
        for (State current : relevantStates) {
            Optional<Boolean> result = expr.evaluate(current);
            if (result.isPresent() && result.get() == false)
                return Optional.of(false);
        }
        // No conflicting state found

        return Optional.of(true);
    }

    public String toString() {
        return "H" + "(" + expr + ")";
    }
}
