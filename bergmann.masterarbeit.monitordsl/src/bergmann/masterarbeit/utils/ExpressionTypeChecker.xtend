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
import bergmann.masterarbeit.mappingdsl.mappingDSL.BinaryJava
import bergmann.masterarbeit.mappingdsl.mappingDSL.Type
import bergmann.masterarbeit.mappingdsl.mappingDSL.JavaType
import bergmann.masterarbeit.mappingdsl.mappingDSL.BaseType
import bergmann.masterarbeit.mappingdsl.mappingDSL.UnaryJava
import bergmann.masterarbeit.monitorDsl.StringLiteral
import bergmann.masterarbeit.monitorDsl.TimeOffset
import bergmann.masterarbeit.monitorDsl.IfThenElse

class ExpressionTypeChecker {
	static var expressionTypeMap = new HashMap<Expression, String>()
	static var BOOLEAN_JAVA_CLASS = "java.lang.Boolean"
	static var NUMBER_JAVA_CLASS = "org.jscience.physics.amount.Amount"
	static var STRING_JAVA_CLASS = "java.lang.String"
	static var OBJECT = "java.lang.Object"
		

	def public static boolean isValid(Expression expr) {
		try {
			return expr.expressionType != ""
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	def public static boolean isBoolean(Expression expr) {
		try {
			return expr.expressionType == BOOLEAN_JAVA_CLASS
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	def public static boolean isString(Expression expr) {
		try {
			return expr.expressionType == STRING_JAVA_CLASS
		} catch (IllegalArgumentException e) {
			return false
		}
	}
	
	def public static boolean isNumber(Expression expr) {
		try {
			return expr.expressionType == NUMBER_JAVA_CLASS
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
					t = BOOLEAN_JAVA_CLASS
			And:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = BOOLEAN_JAVA_CLASS
			Or:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = BOOLEAN_JAVA_CLASS
			LTL_Binary:
				if (expr.left.isBoolean && expr.right.isBoolean)
					t = BOOLEAN_JAVA_CLASS
			LTL_Unary:
				if (expr.expr.isBoolean)
					t = BOOLEAN_JAVA_CLASS
			Rel:
				if ((expr.op == "==" || expr.op == "!="))
					// Only for !=, ==: Any + Any => Boolean
					t = BOOLEAN_JAVA_CLASS
				else if (expr.left.isNumber && expr.right.isNumber)
					// Number + Number => Boolean
					t = BOOLEAN_JAVA_CLASS
			Add:
				if (expr.left.isNumber && expr.right.isNumber)
					t = NUMBER_JAVA_CLASS
			Mult:
				if (expr.left.isNumber && expr.right.isNumber)
					t = NUMBER_JAVA_CLASS
			Negation:
				if ((expr.op == "!" || expr.op == "not") && expr.expr.isBoolean)
					t = BOOLEAN_JAVA_CLASS
				else if (expr.op == "-" && expr.expr.isNumber)
					// Only for -: Number => Number
					t = NUMBER_JAVA_CLASS 
			Subexpression:
				// ( expr ) => Passthrough type
				t =  expr.expr.expressionType
			AggregateExpression:
				if (expr.expr.isNumber)
					t = NUMBER_JAVA_CLASS
			IntLiteral:
				t = NUMBER_JAVA_CLASS
			FloatLiteral:
				t = NUMBER_JAVA_CLASS
			BoolLiteral:
				t = BOOLEAN_JAVA_CLASS
			StringLiteral: t = STRING_JAVA_CLASS
			CrossReference: return expr.handleCrossreference
			MappingUnary: return expr.handleCustomJavaMapping
			MappingBinary: return expr.handleCustomJavaMapping
			TimeOffset: return expr.expr.expressionType
			IfThenElse: {
				if(expr.condition.isBoolean){
					var tThen = expr.then.expressionType
					var tElse = expr.getElse.expressionType
					if(tThen.equals(tElse)){
						t = tThen	
					} else {
						t=""
					}
				} else {
					t=""
				}
			}
			default: {
				throw new IllegalArgumentException("Can't parse expr: " + expr)
			}
		}
		expressionTypeMap.put(expr, t)
		return t		
	}
	
	def static String toExpressionType(BASE_VALUETYPE t){
		switch t{
			case BOOLEAN: return BOOLEAN_JAVA_CLASS
			case NUMBER: return NUMBER_JAVA_CLASS
			case STRING: return STRING_JAVA_CLASS
			default: throw new IllegalArgumentException("Can't parse type: " + t)
		}
	}
	
	def private static String handleCustomJavaMapping(MappingUnary e){
		try {
			var domainElement = e.ref as UnaryJava
			var declaredIn = domainElement.type1.handleDomainType
			var declaredOut = domainElement.type2.handleDomainType
			if(e.expr.expressionType.equals(declaredIn) || e.expr.expressionType.equals(OBJECT))
				return declaredOut
			else
				return ""
		} catch (Exception exception) {
			throw new IllegalArgumentException("Can't parse type of: " + e)
		}
	}
	
	def private static String handleCustomJavaMapping(MappingBinary e){
		try {
			var domainElement = e.ref as BinaryJava
			var declaredLeft = domainElement.type1.handleDomainType
			var declaredRight = domainElement.type2.handleDomainType
			var declaredResult = domainElement.type3.handleDomainType
			var realLeft = e.left.expressionType
			var realRight = e.right.expressionType
			if((realLeft.equals(declaredLeft)|| declaredLeft.equals(OBJECT)) && ( realRight.equals(declaredRight) || declaredRight.equals(OBJECT)))
				return declaredResult
			else
				return ""
		} catch (Exception exception) {
			throw new IllegalArgumentException("Can't parse type of: " + e)
		}
	}
	
	def private static String handleDomainType(Type t){
		switch t{
			JavaType : return t.type.ref.javaType.qualifiedName
			BaseType: return t.type.toExpressionType
			default: throw new IllegalArgumentException("Can't parse type: " + t)
		}
	}
	def private static String getClassname(Object o){
		if (o == null)
			return ""
		else
			return 0.class.name
	}
	
	def private static String handleCrossreference(CrossReference e){
		var referenced = e.ref
		switch referenced {
			UserVariable: return referenced.expr.expressionType 
			DomainValue: return referenced.type.toExpressionType
			LiteralJava: return referenced.type.handleDomainType 
			default: throw new IllegalArgumentException("Typing: Can't parse : " + e)
		}
	}
}
