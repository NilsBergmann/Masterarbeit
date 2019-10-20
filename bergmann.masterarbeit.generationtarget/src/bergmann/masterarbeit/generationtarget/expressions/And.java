package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class And extends BinaryExpression<Boolean, Boolean, Boolean> {

    public And(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        Optional<Boolean> a = this.left.evaluate(state);
        Optional<Boolean> b = this.right.evaluate(state);
        // None are present: Return Nothing
        if (!a.isPresent() && !b.isPresent())
            return Optional.empty();
        // Both are present: Return a && b
        if (a.isPresent() && b.isPresent()) {
            Boolean retVal = a.get() && b.get();
            return Optional.of(retVal);
        }
        // a is not present: if !b return false else null
        if (!a.isPresent())
            if (!b.get())
                return Optional.of(false);
            else
                return Optional.empty();
        // See above
        if (!b.isPresent())
            if (!a.get())
                return Optional.of(false);
            else
                return Optional.empty();
        // Should never reach this part
        System.err.println("Unexpected result in AND expression " + this.toString());
        return Optional.empty();

    }

    public String toString() {
        return "(" + left + " AND " + right + ")";
    }

}