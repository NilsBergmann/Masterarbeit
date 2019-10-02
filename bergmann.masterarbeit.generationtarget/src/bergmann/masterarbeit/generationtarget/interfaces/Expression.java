package bergmann.masterarbeit.generationtarget.interfaces;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;

public abstract class Expression<T> {
    public abstract Optional<T> evaluate(State state, DataController dataSource);
    public Expression(){}; 
}