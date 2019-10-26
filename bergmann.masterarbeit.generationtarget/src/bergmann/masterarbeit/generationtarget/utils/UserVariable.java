package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;


import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class UserVariable<T> extends Expression<T> {
    public Expression<T> expression;
    public String name;

    public UserVariable(String name) {
    	this(name, null);
    }
    
    public UserVariable(String name, Expression<T> expr) {
        this.expression = expr;
        this.name = name;
    }

    public Optional<T> evaluate(State state) {
    	if(expression == null) {
    		System.err.println("No expression set for assertion " + name);
    		return Optional.empty();
    	}
    	Optional<T> result = Optional.empty();
        if(state.getStoredUserVariableIDs().contains(this.name)) {
        	result = state.getStoredUserVariableResult(this.name);
    		if (result != null && result.isPresent())
    			return result;
        }
    	result = expression.evaluate(state);
   		state.storeUserVariableResult(this.name, result);
    	return result;
    }
    
    public void setExpression(Expression<T> expr) {
        this.expression = expr;
    }
    
    public String toString() {
		return "UserVar['" + this.name +"']=" + this.expression.toString();
    }
}