package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class BoolEquality extends BinaryExpression<Boolean, Boolean, Boolean> {
    String operator = null;

    public BoolEquality(Expression<Boolean> left, Expression<Boolean> right, String operator) {
        super(left, right);
        this.operator = operator;
    }

    @Override
    public Optional<Boolean> evaluate(State state, DataController dataSource) {
        Optional<Boolean> a = this.left.evaluate(state, dataSource);
        Optional<Boolean> b = this.right.evaluate(state, dataSource);
        // One is missing: Return Nothing
        if (!a.isPresent() || !b.isPresent())
            return Optional.empty();
        if ("!=".equals(this.operator))
            return Optional.of(a.get() != b.get());
        if ("==".equals(this.operator))
            return Optional.of(a.get() == b.get());
        // Should never reach this part
        System.out.println("Unexpected Result in Expression");
        return Optional.empty();
    }

}