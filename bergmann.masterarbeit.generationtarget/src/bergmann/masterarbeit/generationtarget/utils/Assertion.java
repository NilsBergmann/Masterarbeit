package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Assertion {
    public Expression<Boolean> expression;
    public String name;

    public Assertion(String name, Expression<Boolean> expr) {
        this.expression = expr;
        this.name = name;
    }

    public Optional<Boolean> evaluateAt(State state) {
        Optional<Boolean> result = state.getStored(this.name);
        if(result.isPresent())
        	return result;
        else {
        	result = expression.evaluate(state);
       		state.store(this.name, result);
        	return result;
        }
    }

    public void setExpression(Expression<Boolean> expr) {
        this.expression = expr;
    }
}