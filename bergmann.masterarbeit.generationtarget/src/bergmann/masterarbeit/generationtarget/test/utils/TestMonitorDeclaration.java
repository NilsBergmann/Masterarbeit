package bergmann.masterarbeit.generationtarget.test.utils;

import javax.measure.unit.Unit;

import bergmann.masterarbeit.generationtarget.utils.MonitorDeclaration;

public class TestMonitorDeclaration extends MonitorDeclaration {
	public void addDBBoolean(String name){
		getRequiredDataBooleans().add(name);
	}
	public void addDBString(String name){
		getRequiredDataStrings().add(name);
	}
	public void addDBAmount(String name, Unit unit) {
		getRequiredDataNumbers().put(name, unit);
	}
}
