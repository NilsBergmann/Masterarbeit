package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.BinaryExpression;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Implication extends BinaryExpression<Boolean, Boolean, Boolean> {

    public Implication(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    @Override
    public Optional<Boolean> evaluate(State state) {
        Optional<Boolean> a = this.left.evaluate(state);
        Optional<Boolean> b = this.right.evaluate(state);
        // None are present: Return Nothing
        if (!a.isPresent() && !b.isPresent())
            return Optional.empty();
        // Both are present: Return !a || b
        if (a.isPresent() && b.isPresent()) {
            Boolean retVal = (!a.get()) || b.get();
            return Optional.of(retVal);
        }
        // a is not present: if b return true else null
        if (!a.isPresent())
            if (b.get())
                return Optional.of(true);
            else
                return Optional.empty();
        // b is not present: if !a return true else null
        if (!b.isPresent())
            if (!a.get())
                return Optional.of(true);
            else
                return Optional.empty();
        // Should never reach this part
        System.err.println("Unexpected result in Implication expression " + this.toString());
        return Optional.empty();

    }

    public String toString() {
        return "(" + left + " -> " + right + ") ";
    }

}