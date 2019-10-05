package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class PLTL_Once extends UnaryExpression<Boolean, Boolean> {
    RelativeTimeInterval interval;
    Expression<Boolean> helper;

    public PLTL_Once(Expression<Boolean> expr) {
        this(expr, null);
    }

    public PLTL_Once(Expression<Boolean> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
        this.interval.makeNegative();
        // Once ψ ≡ ¬Historically ¬ψ
        helper = new BoolNegation(new PLTL_Historically(new BoolNegation(expr), interval));
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        return helper.evaluate(state, dataSource);
    }
}