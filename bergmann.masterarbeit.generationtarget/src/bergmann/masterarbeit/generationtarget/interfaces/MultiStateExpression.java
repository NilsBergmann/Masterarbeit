package bergmann.masterarbeit.generationtarget.interfaces;

import java.util.List;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;

/**
 * MultiStateExpression
 */
public abstract class MultiStateExpression<T> extends Expression<T> {

    public Optional<T> evaluate(State state, DataController ctrl) {
        return evaluateForTimeframe(getRelevantStates());
    }

    public abstract List<State> getRelevantStates();

    public abstract Optional<T> evaluateForTimeframe(List<State> states);
}