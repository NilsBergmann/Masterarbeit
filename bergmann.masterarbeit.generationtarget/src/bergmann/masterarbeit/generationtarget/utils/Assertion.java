package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Assertion extends Expression<Boolean> {
    public Expression<Boolean> expression;
    public String name;

    public Assertion(String name) {
    	this(name, null);
    }
    
    public Assertion(String name, Expression<Boolean> expr) {
        this.expression = expr;
        this.name = name;
    }

    public Optional<Boolean> evaluate(State state) {
    	if(expression == null) {
    		System.err.println("No expression set for assertion " + name);
    		return Optional.empty();
    	}
    	Optional<Boolean> result = Optional.empty();
        if(state.getStoredAssertionResults().contains(this.name)) {
        	result = state.getAssertionResult(this.name);
    		if (result != null && result.isPresent())
    			return result;
        }
    	result = expression.evaluate(state);
   		state.storeAssertionResult(this.name, result);
    	return result;
    }

    public void setExpression(Expression<Boolean> expr) {
        this.expression = expr;
    }
    
    public String toString() {
		return "Assertion['" + this.name +"']=" + this.expression.toString();
    }
}