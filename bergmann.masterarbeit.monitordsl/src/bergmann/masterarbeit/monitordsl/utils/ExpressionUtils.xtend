package bergmann.masterarbeit.monitordsl.utils

import bergmann.masterarbeit.monitordsl.monitorDSL.*
import bergmann.masterarbeit.mappingdsl.mappingDSL.*
import java.util.List
import java.util.ArrayList
import java.lang.reflect.Method
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.EObject
import java.util.HashMap
import org.eclipse.xtext.EcoreUtil2

class ExpressionUtils {
	def static List<Expression> getSubexpressions(Expression expr) {
		// Dirty hack to get all subexpressions
		var subExps = new ArrayList<Expression>()
		for (Method m : expr.class.methods) {
			if (m.name == "getExpr" || m.name == "getLeft" || m.name == "getRight" || m.name == "getOptionalExpr") {
				var subExpr = m.invoke(expr)
				if (subExpr != null)
					subExps.add(subExpr as Expression)
			}
		}
		return subExps
	}


	def static String getShortName(Expression expr) {
		switch expr {
			Implication:
				return "Implication"
			And:
				return "And"
			Or:
				return "Or"
			LTL_Binary:
				return expr.op.toString
			LTL_Unary:
				return expr.op.toString
			Rel:
				return expr.op.toString
			Add:
				return expr.op.toString
			Mult:
				return expr.op.toString
			Negation:
				return "Negation"
			Subexpression:
				return expr.expr.getShortName
			AggregateExpression:
				return expr.op.toString
			IntLiteral:
				return "Integer_Literal"
			FloatLiteral:
				return "Float_Literal"
			BoolLiteral:
				return "Boolean_Literal"
			StringLiteral:
				return "String_Literal"
			CrossReference:
				switch expr.ref {
					CustomJava: return (expr.ref as CustomJava).name
					UserVariable: return (expr.ref as UserVariable).name
					default: return expr.toString
				}
			MappingBinary:
				return (expr.ref as BinaryJava).name
			TimeOffset:
				return "Offset"
			IfThenElse:
				return "If-Then-Else"
			default:
				return expr.toString
		}
	}
}
