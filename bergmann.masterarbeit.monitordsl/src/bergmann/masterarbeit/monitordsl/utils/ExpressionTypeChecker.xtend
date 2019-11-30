package bergmann.masterarbeit.monitordsl.utils

import bergmann.masterarbeit.monitordsl.monitorDSL.*
import bergmann.masterarbeit.mappingdsl.mappingDSL.*

import java.lang.reflect.Method
import java.util.Arrays
import java.util.HashMap;
import java.util.Map
import org.eclipse.emf.ecore.EObject

import org.eclipse.emf.ecore.impl.EObjectImpl

class ExpressionTypeChecker {
	static var expressionTypeMap = new HashMap<Expression, String>()
	static var BOOLEAN_JAVA_CLASS = "java.lang.Boolean"
	static var NUMBER_JAVA_CLASS = "org.jscience.physics.amount.Amount"
	static var STRING_JAVA_CLASS = "java.lang.String"
	static var OBJECT = "java.lang.Object"
		

	def public static boolean isValid(Expression expr) {
		try {
			var t =  expr.expressionType
			return t != ""
		} catch (IllegalArgumentException e) {
			System.err.println(e)
			return false
		}
	}
	
	def public static boolean isBoolean(Expression expr) {
		try {
			var t =  expr.expressionType
			return t == BOOLEAN_JAVA_CLASS
		} catch (IllegalArgumentException e) {
			System.err.println(e)
			return false
		}
	}
	
	def public static boolean isString(Expression expr) {
		try {
		    var t =  expr.expressionType
			return t == STRING_JAVA_CLASS
		} catch (IllegalArgumentException e) {
			System.err.println(e)
			return false
		}
	}
	
	def public static boolean isNumber(Expression expr) {
		try {
			var t =  expr.expressionType
			return t == NUMBER_JAVA_CLASS
		} catch (IllegalArgumentException e) {
			System.err.println(e)
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
				if ((expr.op == "==" || expr.op == "!=") && expr.left.isValid && expr.right.isValid)
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
			case ANY: return OBJECT
			default: throw new IllegalArgumentException("Can't parse type: " + t)
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
			if((realLeft.equals(declaredLeft)|| (e.left.isValid && declaredLeft.equals(OBJECT))) && ( realRight.equals(declaredRight) || (e.right.isValid && declaredRight.equals(OBJECT))))
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
			UnaryJava: {
				try {
					var domainElement = e.ref as UnaryJava
					if(e.optionalExpr == null || e.optionalExpr instanceof EObjectImpl)
						throw new IllegalArgumentException("Reference to Java_Unary missing Expression. " + e)
					var declaredIn = domainElement.type1.handleDomainType
					var declaredOut = domainElement.type2.handleDomainType
					if(e.optionalExpr.expressionType.equals(declaredIn) || (e.optionalExpr.isValid && declaredIn.equals(OBJECT)))
						return declaredOut
					else
						return ""
				} catch (Exception exception) {
					return ""
				}
			}
			default: return ""
		}
	}
}
