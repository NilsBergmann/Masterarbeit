package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class StringLiteral extends Expression<String> {
    String value;

    public StringLiteral(String value) {
        super();
        this.value = value;
    }

    public Optional<String> evaluate(State state) {
    	if (value != null)
    		return Optional.of(this.value);
    	else
    		return Optional.empty();
    }
    
    @Override
    public String toString() {
    	return "\""+value+"\"";
    }
}