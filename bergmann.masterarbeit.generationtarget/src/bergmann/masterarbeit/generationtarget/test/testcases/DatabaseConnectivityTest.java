package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.DataController;
import bergmann.masterarbeit.generationtarget.dataaccess.State;
import bergmann.masterarbeit.generationtarget.expressions.BoolDatabaseAccess;
import bergmann.masterarbeit.generationtarget.expressions.BoolLiteral;
import bergmann.masterarbeit.generationtarget.interfaces.Expression;

class DatabaseConnectivityTest {
	private DataController ctrl;
	
	@BeforeEach
	public void init() {
		ctrl = new DataController(false);
		ctrl.connectToDatabase("Testcases.db");
	}
	
	@Test 
	void nonexistingTableTest() {
		System.out.println(ctrl);
		IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, ()->ctrl.selectTable("DOESNTEXIST"));
		assertTrue(e.getMessage().contains("No table"));
	}
	

}
