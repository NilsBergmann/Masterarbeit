package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Z extends UnaryExpression<Boolean, Boolean> {
    RelativeTimeInterval interval;
    Expression<Boolean> helper;

    public PLTL_Z(Expression<Boolean> expr) {
        super(expr);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        State previous = state.dataController.getPreviousState(state);
        if (previous == null) {
            // The Z operator is similar to the Y operator, and it only differs in the way
            // the initial time instant is dealt with: at time zero, Yφ is false, while Zφ
            // is true. [Bounded Verification of Past LTL]
            return Optional.of(true);
        }
        return expr.evaluate(previous);
    }

    public String toString() {
        return "Z" + "(" + expr + ")";
    }
}