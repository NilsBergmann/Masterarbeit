package bergmann.masterarbeit.generationtarget;

import java.util.ArrayList;
import java.util.List;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.DatabaseWrapper;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.utils.Assertion;
import bergmann.masterarbeit.generationtarget.expressions.*;
import bergmann.masterarbeit.generationtarget.utils.UserVariable;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class RunEvaluation {
    public static void main(String args[]) {
        DataController dataControl = new DataController(false);
        dataControl.connectToDatabase("test.db");
        ArrayList<Assertion> assertions = new ArrayList<Assertion>();

        DatabaseWrapper dbWrap = dataControl.getDatabaseWrapper();
        List<String> tables = dbWrap.getTables();
        dbWrap.setTable(tables.get(0));

        List<State> states = dbWrap.getStates();
        for (State state : states) {
            System.out.println("State with timestamp:" + state.timestamp);
        }

    }

}