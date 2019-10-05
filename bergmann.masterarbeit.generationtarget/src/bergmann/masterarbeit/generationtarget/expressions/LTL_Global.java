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
    private Expression<Boolean> helper;

    public LTL_Global(Expression<Boolean> expr) {
        super(expr);
    }

    public LTL_Global(Expression<Boolean> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
        // G ψ ≡ ¬F ¬ψ
        helper = new BoolNegation(new LTL_Finally(new BoolNegation(expr), interval));
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        return helper.evaluate(state, dataSource);
    }
}