package bergmann.masterarbeit.generationtarget.expressions;

import java.util.List;
import java.util.Optional;

import javax.swing.plaf.FontUIResource;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Since extends BinaryExpression<Boolean, Boolean, Boolean> {
    RelativeTimeInterval interval;

    public PLTL_Since(Expression<Boolean> left, Expression<Boolean> right) {
        this(left, right, null);
    }

    public PLTL_Since(Expression<Boolean> left, Expression<Boolean> right, RelativeTimeInterval interval) {
        super(left, right);
        this.interval = interval;
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        // The S(for “Since”) operator is the temporal dual of U(until), so that (x S y)
        // is true iff y holds somewhere in the past and x is true from then up to now.
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

        // Iterate over states
        // - Find first occurance of right==true
        // - After that: left must be true
        Optional<Boolean> foundRight = Optional.of(false);
        for (State currentState : relevantStates) {
            if (foundRight.isPresent()) {
                if (foundRight.get() == false) {
                    Optional<Boolean> rightEval = this.right.evaluate(currentState);
                    // Right wasnt found yet
                    if (!rightEval.isPresent()) {
                        // Right is unknown in this state
                        foundRight = Optional.empty();
                    } else if (rightEval.isPresent() && rightEval.get() == true) {
                        Optional<Boolean> leftEval = this.left.evaluate(currentState);
                        // Right is true in this state
                        foundRight = Optional.of(true);
                        // Check if left is true as well
                        if (!leftEval.isPresent())
                            return Optional.empty();
                        else if (leftEval.get() == false)
                            return Optional.of(false);
                    } else if (rightEval.isPresent() && rightEval.get() == false) {
                        // Right is false in this state
                    }
                } else {
                    // Right was found, from now on left has to be true
                    Optional<Boolean> leftEval = this.left.evaluate(currentState);
                    if (!leftEval.isPresent())
                        return Optional.empty();
                    else if (leftEval.get() == false)
                        return Optional.of(false);
                }
            } else {
                // Right was unknown some time:
                // From now on left must be true all the time, otherwise return unknown
                Optional<Boolean> leftEval = this.left.evaluate(currentState);
                if (!leftEval.isPresent() || leftEval.get() == false)
                    return Optional.empty();
            }
        }

        // No conflict found
        // - Check if right has been found
        // --- 1) If it was: return true
        // --- 2) If it wasnt : return false (?)
        // --- 3) If its unknown: return unknown, unless 2) is actually supposed to
        // return true
        return foundRight;
    }

    public String toString() {
        return "S" + "(" + left + "," + right + ")";
    }
}
