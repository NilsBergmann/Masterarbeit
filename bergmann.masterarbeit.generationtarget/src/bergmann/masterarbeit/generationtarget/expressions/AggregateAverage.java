package bergmann.masterarbeit.generationtarget.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jscience.physics.amount.Amount;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.interfaces.UnaryExpression;
import bergmann.masterarbeit.generationtarget.utils.AbsoluteTimeInterval;
import bergmann.masterarbeit.generationtarget.utils.RelativeTimeInterval;

public class AggregateAverage extends UnaryExpression<Amount, Amount> {
    RelativeTimeInterval interval;

    public AggregateAverage(Expression<Amount> expr, RelativeTimeInterval interval) {
        super(expr);
        this.interval = interval;
    }

    @Override
    public Optional<Amount> evaluate(State state) {
        boolean realTime = state.dataController.isRealTime();
        AbsoluteTimeInterval relevantTime = this.interval.addInstant(state.timestamp);

        // Check if data is complete
        if (realTime && !state.dataController.intervalIsInRange(relevantTime)) {
            return Optional.empty();
        }

        // Get states
        List<State> relevantStates = state.dataController.getStatesInInterval(relevantTime);
        if (relevantStates.size() == 0)
            return Optional.empty();

        // Evaluate expr for every state
        List<Optional<Amount>> results = new ArrayList<Optional<Amount>>();
        for (State current : relevantStates) {
            results.add(expr.evaluate(current));
        }

        // Add them all up
        try {
            Amount sum = null;
            for (Optional<Amount> current : results) {
                if (!current.isPresent())
                    // Value missing? return UNKNOWN
                    return Optional.empty();
                if (sum == null)
                    sum = current.get();
                else
                    sum.plus(current.get());
            }

            // Divide by n
            Amount result = sum.divide(results.size());

            return Optional.of(result);
        } catch (javax.measure.converter.ConversionException e) {
            System.err.println("Mismatched units in average calculation, returning UNKNOWN");
            return Optional.empty();
        }
    }
    
    public String toString() {
    	return "AVG[" +interval.toString() + "](" + expr + ")";
    }
}