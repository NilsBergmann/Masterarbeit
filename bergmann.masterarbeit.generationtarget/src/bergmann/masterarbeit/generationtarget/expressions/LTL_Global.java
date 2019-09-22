package bergmann.masterarbeit.generationtarget.expressions;

import java.util.List;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_Global extends UnaryExpression<Boolean, Boolean> {
    RelativeTimeInterval interval;

    public LTL_Global(Expression<Boolean> expr) {
        super(expr);
    }

    public LTL_Global(Expression<Boolean> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        List<State> relevantStates = null;
        if (this.interval != null) {
            AbsoluteTimeInterval relevantTime = this.interval.addInstant(state.timestamp);
            relevantStates = dataSource.getStatesInInterval(relevantTime);
        } else {
            relevantStates = dataSource.getAllStatesAfter(state);
        }

        for (State current : relevantStates) {
            Optional<Boolean> result = expr.evaluate(current, dataSource);
            if (result.isPresent() && result.get() == false)
                return Optional.of(false);
        }

        // TODO: Handle case where an Interval is given and simulation is real time
        if (dataSource.isRealTime())
            return Optional.of(true);
        else
            return Optional.empty();
    }
}