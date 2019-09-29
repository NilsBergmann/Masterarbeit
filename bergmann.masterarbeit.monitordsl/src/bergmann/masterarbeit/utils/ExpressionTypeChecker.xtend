package bergmann.masterarbeit.utils

import bergmann.masterarbeit.monitorDsl.Add
import bergmann.masterarbeit.monitorDsl.AggregateExpression
import bergmann.masterarbeit.monitorDsl.And
import bergmann.masterarbeit.monitorDsl.BoolLiteral
import bergmann.masterarbeit.monitorDsl.Expression
import bergmann.masterarbeit.monitorDsl.FloatLiteral
import bergmann.masterarbeit.monitorDsl.Implication
import bergmann.masterarbeit.monitorDsl.IntLiteral
import bergmann.masterarbeit.monitorDsl.LTL_Binary
import bergmann.masterarbeit.monitorDsl.LTL_Unary
import bergmann.masterarbeit.monitorDsl.Mult
import bergmann.masterarbeit.monitorDsl.Negation
import bergmann.masterarbeit.monitorDsl.Or
import bergmann.masterarbeit.monitorDsl.Rel
import bergmann.masterarbeit.monitorDsl.Subexpression
import java.lang.reflect.Method
import java.util.Arrays
import java.util.HashMap;
import java.util.Map
import bergmann.masterarbeit.mappingdsl.mappingDSL.DomainValue
import bergmann.masterarbeit.mappingdsl.mappingDSL.BASE_VALUETYPE
import bergmann.masterarbeit.monitorDsl.MappingUnary
import bergmann.masterarbeit.monitorDsl.MappingBinary
import bergmann.masterarbeit.mappingdsl.mappingDSL.CustomJava
import bergmann.masterarbeit.monitorDsl.CrossReference
import org.eclipse.emf.ecore.EObject
import bergmann.masterarbeit.monitorDsl.UserVariable
import bergmann.masterarbeit.mappingdsl.mappingDSL.LiteralJava

class ExpressionTypeChecker {
	static var expressionTypeMap = new HashMap<Expression, String>()
	
		

	def public static boolean isValid(Expression expr) {
		try {
			return expr.expressionType != ""
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	def public static boolean isBoolean(Expression expr) {
		try {
			return expr.expressionType == "BOOLEAN"
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	def public static boolean isString(Expression expr) {
		try {
			return expr.expressionType == "STRING"
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	def public static boolean isNumber(Expression expr) {
		try {
			return expr.expressionType == "NUMBER"
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	
	def static String getExpressionType(Expression expr) {
		if(expressionTypeMap.containsKey(expr)){
			return expressionTypeMap.get(expr)
		}
		var String t = ""
		switch (expr) {
			Implication: 
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = "BOOLEAN"
			And:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = "BOOLEAN"
			Or:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = "BOOLEAN"
			LTL_Binary:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = "BOOLEAN"
			LTL_Unary:
				if (expr.expr.isBoolean)
					t = "BOOLEAN"
			Rel:
				if ((expr.op == "==" || expr.op == "!=") && expr.left.expressionType == expr.right.expressionType)
					// Only for !=, ==: Any + Any => Boolean
					t = "BOOLEAN"
				else if (expr.left.isNumber && expr.right.isNumber)
					// Number + Number => Boolean
					t = "BOOLEAN"
			Add:
				if (expr.left.isNumber && expr.right.isNumber)
					t = "NUMBER"
			Mult:
				if (expr.left.isNumber && expr.right.isNumber)
					t = "NUMBER"
			Negation:
				if ((expr.op == "!" || expr.op == "not") && expr.expr.isBoolean)
					t = "BOOLEAN"
				else if (expr.op == "-" && expr.expr.isNumber)
					// Only for -: Number => Number
					t = "BOOLEAN"
			Subexpression:
				// ( expr ) => Passthrough type
				t =  expr.expr.expressionType
			AggregateExpression:
				if (expr.expr.isNumber)
					t = "NUMBER"
			IntLiteral:
				t = "NUMBER"
			FloatLiteral:
				t = "NUMBER"
			BoolLiteral:
				t = "BOOLEAN"
			CrossReference: return expr.handleCrossreference
			MappingUnary: expr.handleCustomJavaMapping
			MappingBinary: expr.handleCustomJavaMapping
			default: {
				throw new IllegalArgumentException("Can't parse expr: " + expr)
			}
		}
		expressionTypeMap.put(expr, t)
		return t		
	}
	
	def static String toExpressionType(BASE_VALUETYPE t){
		switch t{
			case BOOLEAN: return "BOOLEAN"
			case NUMBER: return "NUMBER"
			case STRING: return "STRING"
			default: throw new IllegalArgumentException("Can't parse type: " + t)
		}
	}
	def static String toJavaType(String expressionType){
		switch expressionType{
			case "BOOLEAN": return "Boolean"
			case "NUMBER": return "Double"
			case "STRING": return "String"
		}
	}
	
	def private static String handleCustomJavaMapping(MappingUnary e){
		throw new IllegalArgumentException("Typing: Can't parse : " + e)
	}
	
		def private static String handleCustomJavaMapping(MappingBinary e){
		throw new IllegalArgumentException("Typing: Can't parse : " + e)
	}
	
	
	def private static String handleCrossreference(CrossReference e){
		var referenced = e.ref
		switch referenced {
			UserVariable: return referenced.expr.expressionType 
			DomainValue: return referenced.type.toExpressionType
			LiteralJava: throw new IllegalArgumentException("Typing: Can't parse : " + referenced)
			default: throw new IllegalArgumentException("Typing: Can't parse : " + e)
		}
	}
}
