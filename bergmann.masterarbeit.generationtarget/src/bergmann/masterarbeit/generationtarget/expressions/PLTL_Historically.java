package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
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
        // TODO: Implement this
        return Optional.empty();
    }
}