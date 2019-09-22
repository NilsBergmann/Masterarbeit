package bergmann.masterarbeit.utils

import bergmann.masterarbeit.mappingdsl.mappingDSL.VALUETYPE
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
import bergmann.masterarbeit.monitorDsl.MappingReference
import bergmann.masterarbeit.monitorDsl.Mult
import bergmann.masterarbeit.monitorDsl.Negation
import bergmann.masterarbeit.monitorDsl.Or
import bergmann.masterarbeit.monitorDsl.Rel
import bergmann.masterarbeit.monitorDsl.Subexpression
import bergmann.masterarbeit.monitorDsl.UserVarReference
import java.lang.reflect.Method
import java.util.Arrays
import java.util.HashMap;
import java.util.Map

class ExpressionTypeChecker {
	static var expressionTypeMap = new HashMap<Expression, ExpressionType>()
	
	public enum ExpressionType {
		NUMBER,
		BOOLEAN,
		STRING,
		UNKNOWN
	}
	def public static String toString(ExpressionType t){
		switch(t){
			case ExpressionType.NUMBER: return "number"
			case ExpressionType.BOOLEAN: return "boolean"
			case ExpressionType.STRING: return "string"
			default: return "UNKNOWN"
		}
	}
	
	def public static toExpressionType(VALUETYPE src) {
		// Converts MappingDSL types
		switch (src) {
			case BOOLEAN: return ExpressionType.BOOLEAN
			case NUMBER: return ExpressionType.NUMBER
			case STRING: return ExpressionType.STRING
			default: return ExpressionType.UNKNOWN
		}
	}
	
	def public static String toJavaType(ExpressionType t) {
		switch(t){
			case ExpressionType.NUMBER: return "Double"
			case ExpressionType.BOOLEAN: return "Boolean"
			case ExpressionType.STRING: return "String"
			default: throw new Exception()
		}
	}

	def public static boolean isValid(Expression expr) {
		return expr.expressionType != ExpressionType.UNKNOWN
	}
	
	def public static boolean isBoolean(Expression expr) {
		return expr.expressionType == ExpressionType.BOOLEAN
	}
	
	def public static boolean isString(Expression expr) {
		return expr.expressionType == ExpressionType.STRING
	}
	
	def public static boolean isNumber(Expression expr) {
		return expr.expressionType == ExpressionType.NUMBER
	}
	
	
	def static ExpressionType getExpressionType(Expression expr) {
		if(expressionTypeMap.containsKey(expr)){
			return expressionTypeMap.get(expr)
		}
		var ExpressionType t = ExpressionType.UNKNOWN
		switch (expr) {
			Implication: 
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = ExpressionType.BOOLEAN
			And:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = ExpressionType.BOOLEAN
			Or:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = ExpressionType.BOOLEAN
			LTL_Binary:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = ExpressionType.BOOLEAN
			LTL_Unary:
				if (expr.expr.isBoolean)
					t = ExpressionType.BOOLEAN
			Rel:
				if ((expr.op == "==" || expr.op == "!=") && expr.left.expressionType == expr.right.expressionType)
					// Only for !=, ==: Any + Any => Boolean
					t = ExpressionType.BOOLEAN
				else if (expr.left.isNumber && expr.right.isNumber)
					// Number + Number => Boolean
					t = ExpressionType.BOOLEAN
			Add:
				if (expr.left.isNumber && expr.right.isNumber)
					t = ExpressionType.NUMBER
			Mult:
				if (expr.left.isNumber && expr.right.isNumber)
					t = ExpressionType.NUMBER
			Negation:
				if ((expr.op == "!" || expr.op == "not") && expr.expr.isBoolean)
					t = ExpressionType.BOOLEAN
				else if (expr.op == "-" && expr.expr.isNumber)
					// Only for -: Number => Number
					t = ExpressionType.BOOLEAN
			Subexpression:
				// ( expr ) => Passthrough type
				t =  expr.expr.expressionType
			AggregateExpression:
				if (expr.expr.isNumber)
					t = ExpressionType.NUMBER
			UserVarReference:
				t = expr.ref.expr.expressionType
			MappingReference:
				t = expr.ref.type.toExpressionType
			IntLiteral:
				t = ExpressionType.NUMBER
			FloatLiteral:
				t = ExpressionType.NUMBER
			BoolLiteral:
				t = ExpressionType.BOOLEAN
			default: {
				println("Unknown Expression of type: " + expr.class)
				t = ExpressionType.UNKNOWN
			}
		}
		expressionTypeMap.put(expr, t)
		return t		
	}
}
