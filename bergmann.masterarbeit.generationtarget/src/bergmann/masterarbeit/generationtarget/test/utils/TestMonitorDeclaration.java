package bergmann.masterarbeit.generationtarget.test.utils;

import javax.measure.unit.Unit;

import bergmann.masterarbeit.generationtarget.utils.MonitorDeclaration;

public class TestMonitorDeclaration extends MonitorDeclaration {
	public void addDomainBoolean(String name) {
		getRequiredDataBooleans().add(name);
	}

	public void addDomainString(String name) {
		getRequiredDataStrings().add(name);
	}

	public void addDomainAmount(String name, Unit unit) {
		getRequiredDataNumbers().put(name, unit);
	}
}
