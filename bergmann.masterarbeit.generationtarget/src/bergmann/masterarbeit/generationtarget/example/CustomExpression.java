package bergmann.masterarbeit.generationtarget.example;

import java.util.Optional;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.*;

public class CustomExpression extends UnaryExpression<Boolean, Double> {

    public CustomExpression(Expression<Boolean> expr) {
        super(expr);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Optional<Double> evaluate(State state, DataController dataSource) {
        // TODO Auto-generated method stub
        return null;
    }

}