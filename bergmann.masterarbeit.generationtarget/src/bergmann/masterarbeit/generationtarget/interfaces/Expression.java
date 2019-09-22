package bergmann.masterarbeit.generationtarget.interfaces;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;

public interface Expression<T> {
    public Optional<T> evaluate(State state, DataController dataSource);

}