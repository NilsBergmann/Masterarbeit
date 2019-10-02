package bergmann.masterarbeit.generationtarget.expressions;

import java.util.Optional;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.DatabaseWrapper;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

public class NumberDatabaseAccess implements Expression<Amount> {
    public String columnName;
    public Unit unit;

    public NumberDatabaseAccess(String columnName, Unit unit) {
        super();
        this.columnName = columnName;
        this.unit = unit;
    }

    public NumberDatabaseAccess(String columnName) {
        this(columnName, Unit.ONE);
    }

    public Optional<Amount> evaluate(State state, DataController dataSource) {
        DatabaseWrapper dbWrap = dataSource.getDatabaseWrapper();
        Optional<Amount> retVal = dbWrap.getAmount(state, this.columnName, this.unit);
        return retVal;
    }
}