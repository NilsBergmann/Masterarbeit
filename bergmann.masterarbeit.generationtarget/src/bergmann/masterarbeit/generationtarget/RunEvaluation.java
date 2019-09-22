package bergmann.masterarbeit.generationtarget;

import java.time.Instant;
import java.util.ArrayList;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.expressions.*;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;
import bergmann.masterarbeit.generationtarget.utils.Assertion;
import bergmann.masterarbeit.generationtarget.utils.UserVariable;

class RunEvaluation {
    public static void main(String args[]) {
        DataController dataControl = new DataController();
        dataControl.connectToDatabase("test.db");
        ArrayList<Assertion> assertions = new ArrayList<Assertion>();
    }

}