/*
 * generated by Xtext 2.19.0
 */
package bergmann.masterarbeit.monitordsl.validation

import bergmann.masterarbeit.mappingdsl.mappingDSL.Domain
import bergmann.masterarbeit.monitordsl.monitorDSL.Add
import bergmann.masterarbeit.monitordsl.monitorDSL.Assertion
import bergmann.masterarbeit.monitordsl.monitorDSL.Expression
import bergmann.masterarbeit.monitordsl.monitorDSL.IfThenElse
import bergmann.masterarbeit.monitordsl.monitorDSL.ImportDomain
import bergmann.masterarbeit.monitordsl.monitorDSL.ImportMonitor
import bergmann.masterarbeit.monitordsl.monitorDSL.LTL_Binary
import bergmann.masterarbeit.monitordsl.monitorDSL.LTL_Unary
import bergmann.masterarbeit.monitordsl.monitorDSL.MonitorDSLPackage
import bergmann.masterarbeit.monitordsl.monitorDSL.Monitors
import bergmann.masterarbeit.monitordsl.monitorDSL.Rel
import bergmann.masterarbeit.monitordsl.monitorDSL.TimeIntervalSimple
import bergmann.masterarbeit.monitordsl.monitorDSL.UserVariable
import java.util.StringJoiner
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.validation.Check

import static extension bergmann.masterarbeit.monitordsl.utils.ExpressionTypeChecker.*
import static extension bergmann.masterarbeit.monitordsl.utils.ExpressionUtils.*
import static extension bergmann.masterarbeit.monitordsl.utils.ImportUtils.*
import static extension bergmann.masterarbeit.monitordsl.utils.TimeUtils.*
import static extension bergmann.masterarbeit.monitordsl.utils.UnitUtils.*

/**
 * This class contains custom validation rules.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class MonitorDSLValidator extends AbstractMonitorDSLValidator {

//	public static val INVALID_NAME = 'invalidName'
//
//	@Check
//	def checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.name.charAt(0))) {
//			warning('Name should start with a capital',
//					MonitorDSLPackage.Literals.GREETING__NAME,
//					INVALID_NAME)
//		}
//	}
	@Check
	def checkExpressionType(UserVariable userVar) {
		if (!userVar.expr.isValid) {
			markSubexpressions(userVar.expr)
			error("Invalid User Variable. Can not resolve to a valid type",
				MonitorDSLPackage.Literals.USER_VARIABLE__NAME)
		}
	}

	@Check
	def checkExpressionType(Assertion assertion) {
		if (!assertion.expr.isBoolean) {
			markSubexpressions(assertion.expr)
			error("Invalid Assertion. Can not resolve expression to boolean",
				MonitorDSLPackage.Literals.ASSERTION__NAME)
		}
	}

	def void markSubexpressions(Expression expr) {
		if (!expr.isValid) {
			var invalidSubexpressionFound = false
			var subexpressions = expr.subexpressions
			for (Expression subExpr : subexpressions) {
				if (subExpr != null && !subExpr.isValid) {
					markSubexpressions(subExpr)
					invalidSubexpressionFound = true
				}
			}
			if (!invalidSubexpressionFound) {
				// Error must be in how this expression is used, not in some subexpression
				var errorString = "Subexpression can't be resolved to a valid type. "
				errorString += expr.shortName + " is not defined"

				if (subexpressions.size != 0) {
					var joiner = new StringJoiner(" , ", "[", "]")
					for (Expression subExpr : subexpressions) {
						joiner.add(subExpr.expressionType)
					}
					errorString += " for given inputs " + joiner.toString
				}
				error(errorString, expr.eContainer, expr.eContainingFeature, -1)
			}
		}
	}

	@Check
	def checkTimeIntervals(TimeIntervalSimple t) {
		if (!(t.start.toMillisec <= t.end.toMillisec)) {
			error("Interval start must be before end", t.eContainer, t.eContainingFeature, -1)
		}
	}

	@Check
	def checkTimeIntervals(LTL_Binary expr) {
		if (expr.time == null) {
			return
		}
		if (expr.time.isZero) {
			warning("Given interval equals zero and can be removed.", MonitorDSLPackage.Literals.LTL_BINARY__TIME)
			return
		}
		if (expr.time.containsNegative && expr.time.containsPositive) {
			error("Mismatching signs for start and end are disallowed for '" + expr.op + "'.",
				MonitorDSLPackage.Literals.LTL_BINARY__TIME)
			return
		}
		switch expr.op {
			// LTL
			case UNTIL,
			case WEAK_UNTIL,
			case RELEASE: {
				if (expr.time.containsNegative) {
					error("Negative time interval for LTL operator.", MonitorDSLPackage.Literals.LTL_BINARY__TIME)
				}
			}
			// PLTL
			case TRIGGER,
			case SINCE: {
				if (expr.time.containsPositive) {
					error("Positive time interval for PLTL operator.", MonitorDSLPackage.Literals.LTL_BINARY__TIME)
				}
			}
			// default
			default:
				throw new IllegalArgumentException("Unknown binary (P)LTL operator " + expr.op)
		}
	}

	@Check
	def checkTimeIntervals(LTL_Unary expr) {
		if (expr.time == null) {
			return
		}
		if (expr.time.isZero) {
			warning("Given interval equals zero and can be removed.", MonitorDSLPackage.Literals.LTL_UNARY__TIME)
			return
		}
		if (expr.time.containsNegative && expr.time.containsPositive) {
			error("Mismatching signs for start and end are disallowed for '" + expr.op + "'.",
				MonitorDSLPackage.Literals.LTL_UNARY__TIME)
			return
		}
		switch expr.op {
			// LTL
			case NEXT,
			case FINALLY,
			case GLOBAL: {
				if (expr.time.containsNegative) {
					error("Negative time interval for LTL operator.", MonitorDSLPackage.Literals.LTL_UNARY__TIME)
				}
			}
			// PLTL
			case YESTERDAY,
			case Z,
			case HISTORICALLY,
			case ONCE: {
				if (expr.time.containsPositive) {
					error("Positive time interval for PLTL operator.", MonitorDSLPackage.Literals.LTL_UNARY__TIME)
				}
			}
			// default
			default:
				throw new IllegalArgumentException("Unknown binary (P)LTL operator " + expr.op)
		}
	}

	@Check
	def unitMismatch(Add expr) {
		if (! (expr.left.isNumber && expr.right.isNumber))
			return
		var comp = expr.left.isUnitCompatible(expr.right)
		if (!comp) {
			var lUnit = expr.left.unit
			var rUnit = expr.right.unit
			error("Incompatible units for operator " + expr.op + "\n\n[" + lUnit + "] " + expr.op + " [" + rUnit + "]",
				expr.eContainer, expr.eContainingFeature, -1)
		}
	}

	@Check
	def unitMismatch(Rel expr) {
		if (! (expr.left.isNumber && expr.right.isNumber))
			return
		var comp = expr.left.isUnitCompatible(expr.right)
		if (!comp) {
			var lUnit = expr.left.unit
			var rUnit = expr.right.unit
			error("Incompatible units for operator " + expr.op + "\n\n[" + lUnit + "] " + expr.op + " [" + rUnit + "]",
				expr.eContainer, expr.eContainingFeature, -1)
		}
	}

	@Check
	def typeMismatch(IfThenElse expr) {
		var tThen = expr.then.expressionType
		var tElse = expr.getElse.expressionType
		if (! tThen.equals(tElse)) {
			error("Different, incompatible types for then and else\n then:[" + tThen + "] else:[" + tElse + "]",
				expr.eContainer, expr.eContainingFeature, -1)
		}
	}

	@Check
	def unitMismatch(IfThenElse expr) {
		if (! (expr.then.isNumber && expr.getElse.isNumber))
			return
		var comp = expr.then.isUnitCompatible(expr.getElse)
		if (!comp) {
			var lUnit = expr.then.unit
			var rUnit = expr.getElse.unit
			error("Different, incompatible units for then and else\n then:[" + lUnit + "] else:[" + rUnit + "]",
				expr.eContainer, expr.eContainingFeature, -1)
		}
	}

	@Check
	def EqualsTypeWarning(Rel expr) {
		if (expr.op.equals("==") || expr.op.equals("!="))
			if (!expr.left.expressionType.equals(expr.right.expressionType))
				warning(
					"Comparing two different datatypes: " + expr.left.expressionType + " and " +
						expr.right.expressionType + ". Resulting behaviour may be unpredictable", expr.eContainer,
					expr.eContainingFeature, -1)
	}

	@Check
	def checkNamesAreUnique(ImportMonitor imp) {
		var containingMonitor = EcoreUtil.getRootContainer(imp) as Monitors
		var importedNames = (EcoreUtil.getRootContainer(imp.ref) as Monitors).allNamedObjectsRecursive
		for (imported : importedNames.entrySet()) {
			// Check UserVars & Assertions
			for (current : containingMonitor.allNamedObjects.entrySet) {
				if (imported.value.equals(current.value) && (!imported.key.equals(current.key))) {
					error("Import of "+imp.ref.name + " causes duplicate definition of " + current.value, imp,
						MonitorDSLPackage.Literals.IMPORT_MONITOR__REF)
				}
			}
			// Check imported domains
			for (importedDomain : containingMonitor.nonRecursiveImportedDomains) {
				for (current : importedDomain.allNamedObjects.entrySet) {
					if (imported.value.equals(current.value) && (!imported.key.equals(current.key))) {
						error("Import of "+imp.ref.name + " causes duplicate definition of " + current.value, imp,
							MonitorDSLPackage.Literals.IMPORT_MONITOR__REF)
					}
				}
			}
			// Check imported monitors
			for (importedMonitor : containingMonitor.nonRecursiveImportedMonitors) {
				if (importedMonitor != imp.ref) {
					for (current : (importedMonitor as Monitors).allNamedObjectsRecursive.entrySet) {
						if (imported.value.equals(current.value) && (!imported.key.equals(current.key))) {
							error("Import of "+imp.ref.name + " causes duplicate definition of " + current.value, imp,
								MonitorDSLPackage.Literals.IMPORT_MONITOR__REF)
						}
					}
				}
			}
		}
	}

	@Check
	def checkNamesAreUnique(ImportDomain imp) {
		var containingMonitor = EcoreUtil.getRootContainer(imp) as Monitors
		var importedNames = (EcoreUtil.getRootContainer(imp.ref) as Domain).allNamedObjects
		for (imported : importedNames.entrySet()) {
			// Check UserVars & Assertions
			for (current : containingMonitor.allNamedObjects.entrySet) {
				if (imported.value.equals(current.value) && (!imported.key.equals(current.key))) {
					error("Import of "+imp.ref.name + " causes duplicate definition of " + current.value, imp,
						MonitorDSLPackage.Literals.IMPORT_DOMAIN__REF)
				}
			}
			// Check imported domains
			for (importedDomain : containingMonitor.nonRecursiveImportedDomains) {
				if (importedDomain != imp.ref) {
					for (current : importedDomain.allNamedObjects.entrySet) {
						if (imported.value.equals(current.value) && (!imported.key.equals(current.key))) {
							error("Import of "+imp.ref.name + " causes duplicate definition of " + current.value, imp,
								MonitorDSLPackage.Literals.IMPORT_DOMAIN__REF)
						}
					}
				}
			}
			// Check imported monitors
			for (importedMonitor : containingMonitor.nonRecursiveImportedMonitors) {
				for (current : (importedMonitor as Monitors).allNamedObjectsRecursive.entrySet) {
					if (imported.value.equals(current.value) && (!imported.key.equals(current.key))) {
						error("Import of "+imp.ref.name + " causes duplicate definition of " + current.value, imp,
							MonitorDSLPackage.Literals.IMPORT_DOMAIN__REF)
					}
				}
			}
		}
	}

	@Check
	def checkNameIsUnused(Assertion ass) {
		var containingMonitor = EcoreUtil.getRootContainer(ass) as Monitors
		var names = containingMonitor.allNamedObjectsRecursive
		for (pair : names.entrySet) {
			var name = pair.value
			var obj = pair.key
			if (name.equals(ass.name) && !obj.equals(ass)) {
				error(ass.name + " is already in use", ass, MonitorDSLPackage.Literals.ASSERTION__NAME)
			}
		}
	}

	@Check
	def checkNameIsUnused(UserVariable uv) {
		var containingMonitor = EcoreUtil.getRootContainer(uv) as Monitors
		var names = containingMonitor.allNamedObjectsRecursive
		for (pair : names.entrySet) {
			var name = pair.value
			var obj = pair.key
			if (name.equals(uv.name) && !obj.equals(uv)) {
				error(uv.name + " is already in use", uv, MonitorDSLPackage.Literals.USER_VARIABLE__NAME)
			}
		}
	}

	@Check
	def checkSelfImports(ImportMonitor imp) {
		var containingMonitor = EcoreUtil.getRootContainer(imp) as Monitors
		var referenced = EcoreUtil.getRootContainer(imp.ref)
		var referencedMonitor = referenced as Monitors
		if (containingMonitor.equals(referenced)) {
			error("Can't import self", imp, MonitorDSLPackage.Literals.IMPORT_MONITOR__REF)
		}
		if (referencedMonitor.recursiveImportedMonitors.contains(containingMonitor)) {
			error("Cyclic import", imp, MonitorDSLPackage.Literals.IMPORT_MONITOR__REF)
		}
	}
}
