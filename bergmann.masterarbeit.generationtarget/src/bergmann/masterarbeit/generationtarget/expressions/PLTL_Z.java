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
        // ¬Zϕ ≡ Y¬ϕ
        // Zϕ ≡ ¬(Y¬ϕ)
        helper = new BoolNegation(new PLTL_Yesterday(new BoolNegation(expr)));
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        return helper.evaluate(state, dataSource);
    }
}