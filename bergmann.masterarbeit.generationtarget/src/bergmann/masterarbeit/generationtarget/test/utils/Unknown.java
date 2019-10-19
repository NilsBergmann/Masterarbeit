package bergmann.masterarbeit.generationtarget.test.utils;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class Unknown<T> extends Expression<T> {
	
	@Override
	public Optional<T> evaluate(State state) {
		return Optional.empty();
	}
	
}