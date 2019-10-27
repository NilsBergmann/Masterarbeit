package bergmann.masterarbeit.generationtarget.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.measure.unit.Unit;

import bergmann.masterarbeit.generationtarget.dataaccess.State;

public abstract class MonitorDeclaration {
	private List<Assertion> assertionExpressions;
	private List<UserVariable> userVarExpressions;
    private Map<String, Unit> requiredDataNumbers;
    private List<String> requiredDataStrings;
    private List<String> requiredDataBooleans;
	private String name = "UNTITLED";
	
	public MonitorDeclaration() {
		// TODO Auto-generated constructor stub
		assertionExpressions = new ArrayList<Assertion>();
		userVarExpressions = new  ArrayList<UserVariable>();
		requiredDataNumbers = new HashMap<String, Unit>();
		requiredDataStrings = new ArrayList<String>();
		requiredDataBooleans = new ArrayList<String>();
	}
	
	public Map<String, Optional> evaluateAllAt(State state){
		Map resultMap = new HashMap<String, Optional>();
		resultMap.putAll(this.evaluateUserVariablesAt(state));
		resultMap.putAll(this.evaluateAssertionsAt(state));
		return resultMap;
	}
	
	public Map<String, Optional<Boolean>> evaluateAssertionsAt(State state){
		Map resultMap = new HashMap<String, Optional>();
		for (Assertion assertion : assertionExpressions) {
			Optional result = assertion.evaluate(state);
			resultMap.put(assertion.name, result);
		}
		return resultMap;
	}
	
	public Map<String, Optional> evaluateUserVariablesAt(State state){
		Map resultMap = new HashMap<String, Optional>();
		for (UserVariable var : userVarExpressions) {
			Optional result = var.evaluate(state);
			resultMap.put(var.name, result);
		}
		return resultMap;
	}

	public List<Assertion> getAssertionExpressions() {
		return assertionExpressions;
	}

	public void setAssertionExpressions(List<Assertion> assertionExpressions) {
		this.assertionExpressions = assertionExpressions;
	}

	public List<UserVariable> getUserVarExpressions() {
		return userVarExpressions;
	}

	public void setUserVarExpressions(List<UserVariable> userVarExpressions) {
		this.userVarExpressions = userVarExpressions;
	}

	public Map<String, Unit> getRequiredDataNumbers() {
		return requiredDataNumbers;
	}

	public void setRequiredDataNumbers(Map<String, Unit> requiredDataNumbers) {
		this.requiredDataNumbers = requiredDataNumbers;
	}

	public List<String> getRequiredDataStrings() {
		return requiredDataStrings;
	}

	public void setRequiredDataStrings(List<String> requiredDataStrings) {
		this.requiredDataStrings = requiredDataStrings;
	}

	public List<String> getRequiredDataBooleans() {
		return requiredDataBooleans;
	}

	public void setRequiredDataBooleans(List<String> requiredDataBooleans) {
		this.requiredDataBooleans = requiredDataBooleans;
	}
	
	public Set<String> getDeclaredUserVariableNames(){
		Set<String> result = new HashSet<String>();
		for (UserVariable uV : this.userVarExpressions) {
			result.add(uV.name);
		}
		return result;
	}
	
	public Set<String> getDeclaredAssertionNames(){
		Set<String> result = new HashSet<String>();
		for (Assertion ass : this.assertionExpressions) {
			result.add(ass.name);
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
