/*
 * generated by Xtext 2.12.0
 */
package bergmann.masterarbeit.validation

import bergmann.masterarbeit.monitorDsl.Assertion
import bergmann.masterarbeit.monitorDsl.MonitorDslPackage
import bergmann.masterarbeit.monitorDsl.UserVariable
import org.eclipse.xtext.validation.Check
import bergmann.masterarbeit.monitorDsl.Expression
import bergmann.masterarbeit.monitorDsl.*
import bergmann.masterarbeit.utils.ExpressionTypeChecker
import static extension bergmann.masterarbeit.utils.ExpressionUtils.*
import static extension bergmann.masterarbeit.utils.ExpressionTypeChecker.*
import static extension bergmann.masterarbeit.utils.UnitUtils.*

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class MonitorDslValidator extends AbstractMonitorDslValidator {
//	public static val INVALID_NAME = 'invalidName'
//
//	@Check 
//	def checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.name.charAt(0))) {
//			warning('Name should start with a capital', 
//					MonitorDslPackage.Literals.GREETING__NAME,
//					INVALID_NAME)
//		}
//	}
	@Check
	def checkExpressionType(UserVariable userVar){
		if(!userVar.expr.isValid){
			markSubexpressions(userVar.expr)
			error("Invalid User Variable. Can not resolve to a valid type", MonitorDslPackage.Literals.USER_VARIABLE__NAME)
		} 
	} 
	
	@Check
	def checkExpressionType(Assertion assertion){
		if(!assertion.expr.isBoolean){
			markSubexpressions(assertion.expr)
			error("Invalid Assertion. Can not resolve expression to boolean", MonitorDslPackage.Literals.ASSERTION__NAME)
		} 
	}
	
	def markSubexpressions(Expression expr){
		print("Checking " + expr)
		if(!expr.isValid){
			var invalidSubexpressionFound = false
			for(Expression subExpr : expr.subexpressions){
				if(!subExpr.isValid){
					markSubexpressions(subExpr)
					invalidSubexpressionFound = true
				}
			}
			if(!invalidSubexpressionFound){
				// Error must be in how this expression is used, not in some subexpression
				error("Expression can't be resolved to a valid type", expr.eContainer, expr.eContainingFeature, -1)
			}
		}
	}
	
	@Check
	def checkTimeIntervals(TimeIntervalSimple t){
		// TODO: Implement this
	}
	
	@Check 
	def checkUnits(Expression expr){
		switch expr {
			// TODO: Check if Units are compatible
		}
	}
	
	
	@Check 
	def checkNamesAreUnique(Monitors monitors){
		//TODO: Implement this
	}
}

