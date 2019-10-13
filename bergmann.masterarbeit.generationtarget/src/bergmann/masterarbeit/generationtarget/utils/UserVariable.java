package bergmann.masterarbeit.generationtarget.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class UserVariable<T> extends Expression<T> {
    public Expression<T> expression;
    public String name;

    public UserVariable(String name, Expression<T> expr) {
        this.expression = expr;
        this.name = name;
    }

    public Optional<T> evaluate(State state) {
        Optional<T> result = state.getStored(this.name);
        if(result.isPresent())
        	return result;
        else {
        	result = expression.evaluate(state);
       		state.store(this.name, result);
        	return result;
        }
    }
}