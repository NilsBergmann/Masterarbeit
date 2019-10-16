package bergmann.masterarbeit.generationtarget.expressions;

import java.util.List;
import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class LTL_Release extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;

    public LTL_Release(Expression<Boolean> left, Expression<Boolean> right) {
        this(left, right, null);
    }

    public LTL_Release(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        /**
         * Wikipedia: ψ R φ -> φ has to be true until and including the point where ψ
         * first becomes true; if ψ never becomes true, φ must remain true forever.
         */
        List<State> relevantStates = null;
        boolean hasInterval = this.interval != null;
        boolean realTime = state.dataController.isRealTime();

        // Get relevant states
        if (hasInterval) {
            AbsoluteTimeInterval relevantTime = this.interval.addInstant(state.timestamp);
            relevantStates = state.dataController.getStatesInInterval(relevantTime);
        } else {
            relevantStates = state.dataController.getAllStatesAfter(state);
            relevantStates.add(0, state);
        }

        Optional<Boolean> leftWasTrueOnce = Optional.of(false);
        for (State current : relevantStates) {
            // Check if any state has right = false before left becomes true at least once
            if (leftWasTrueOnce.isPresent() && leftWasTrueOnce.get().equals(false)) {
                // Left was true for at least one state
                // If right is unknown, return unknown
                // If right is false, return false
                // Then continue loop
                Optional<Boolean> resultLeft = left.evaluate(state);
                Optional<Boolean> resultRight = left.evaluate(state);
                if (!resultRight.isPresent())
                    return Optional.empty();
                if (resultRight.get().equals(false))
                    return Optional.of(false);
                // Update knowledge about left
                if (!resultLeft.isPresent())
                    leftWasTrueOnce = Optional.empty();
                if (resultLeft.get().equals(true))
                    leftWasTrueOnce = Optional.of(true);
            } else if (!leftWasTrueOnce.isPresent()) {
                // left value was unknown for at least one state.
                // If right is unknown, return unknown
                // If right is false, return unknown
                // Then continue loop
                Optional<Boolean> resultLeft = left.evaluate(state);
                Optional<Boolean> resultRight = left.evaluate(state);
                if (!resultRight.isPresent())
                    return Optional.empty();
                if (resultRight.get().equals(false))
                    return Optional.empty();
                // Update knowledge about left
                if (!resultLeft.isPresent())
                    leftWasTrueOnce = Optional.empty();
                if (resultLeft.get().equals(true))
                    leftWasTrueOnce = Optional.of(true);
            } else if (leftWasTrueOnce.isPresent() && leftWasTrueOnce.get().equals(true)) {
                break;
            }
            // if left is known to have been true: do nothing, everything is okay
        }
        if (leftWasTrueOnce.isPresent() && leftWasTrueOnce.get().equals(true)) {
            // left was released and the loop hasnt been cancelled by a conflicting right
            return Optional.of(true);
        }

        // No conflicting state found so far, but right (maybe) wasnt released yet

        // Handle different evaluation cases
        if (hasInterval) {
            // Expr has interval
            if (realTime) {
                // Realtime mode -> There might be future states coming
                if (state.dataController.intervalIsInRange(this.interval.addInstant(state.timestamp))) {
                    // Interval is completely inside timeframe of known data when evaluating
                    return leftWasTrueOnce.isPresent() ? Optional.of(true) : Optional.empty();
                } else {
                    // There might be some future state where right is false
                    return Optional.empty();
                }
            } else {
                // Non realtime mode -> No more future states coming
                return leftWasTrueOnce.isPresent() ? Optional.of(true) : Optional.empty();
            }
        } else {
            // Expr doesnt have interval
            if (realTime) {
                // Realtime mode -> A future state might cause an evaluation to false
                return Optional.empty();
            } else {
                // Non realtime mode -> No more future states coming
                return leftWasTrueOnce.isPresent() ? Optional.of(true) : Optional.empty();
            }
        }
    }

    public String toString() {
        return "R" + "(" + left + "," + right + ")";
    }
}