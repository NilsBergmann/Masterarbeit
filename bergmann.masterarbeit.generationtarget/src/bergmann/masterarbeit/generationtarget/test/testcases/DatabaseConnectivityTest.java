package bergmann.masterarbeit.generationtarget.test.testcases;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bergmann.masterarbeit.generationtarget.dataaccess.StandaloneDataController;

class DatabaseConnectivityTest {
	private StandaloneDataController ctrl;
	
	@BeforeEach
	public void init() {
		ctrl = new StandaloneDataController(false);
		ctrl.connectToDatabase("Testcases.db");
	}
	
	@Test 
	void nonexistingTableTest() {
		System.out.println(ctrl);
		IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, ()->ctrl.selectTable("DOESNTEXIST"));
		assertTrue(e.getMessage().contains("No table"));
	}
	

}
